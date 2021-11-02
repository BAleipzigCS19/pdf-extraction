package de.baleipzig.pdfextraction.backend.rest;

import de.baleipzig.pdfextraction.backend.entities.ExtractionTemplate;
import de.baleipzig.pdfextraction.backend.entities.Field;
import de.baleipzig.pdfextraction.backend.entities.ResultTemplate;
import de.baleipzig.pdfextraction.backend.repositories.ResultRepository;
import de.baleipzig.pdfextraction.backend.repositories.TemplateRepository;
import de.baleipzig.pdfextraction.backend.tesseract.Tess;
import de.baleipzig.pdfextraction.backend.util.FieldUtils;
import de.baleipzig.pdfextraction.backend.workunits.CombinedExtractedWU;
import de.baleipzig.pdfextraction.backend.workunits.PDFBoxImageWU;
import de.baleipzig.pdfextraction.backend.workunits.XDocWU;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Map;


@RestController
@RequestMapping("/rest/extraction")
public class ExtractionController {

    private final TemplateRepository templateRepository;
    private final ResultRepository resultRepository;
    private final Tess tess;

    public ExtractionController(final TemplateRepository repo,
                                final ResultRepository resultRepository,
                                final Tess tess) {
        this.templateRepository = repo;
        this.resultRepository = resultRepository;
        this.tess = tess;
    }

    /**
     * Creates a test image of the given PDF-File with the Fields of the given template
     *
     * @param templateName Template to use
     * @param content      PDF-File to render the Page of
     * @return Base64 encoded String of the PNG-Image Bytes
     */
    @PostMapping(value = "test", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> createTestImage(@RequestPart("name") final String templateName,
                                                  @RequestPart("content") final byte[] content) {
        final ResponseEntity<String> errorResponse = checkRequest(templateName, content);
        if (errorResponse != null) {
            return errorResponse;
        }


        try {
            final ExtractionTemplate template = this.templateRepository.findTemplateByName(templateName);
            final byte[] imageBytes = new PDFBoxImageWU(template.getFields(), content).work();
            final String imageEncoded = Base64.getEncoder().encodeToString(imageBytes);

            return ResponseEntity.ok().body(imageEncoded);
        } catch (final Exception e) {
            LoggerFactory.getLogger(getClass())
                    .error("Exception while creating test page.", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Attempts to extract Information from the given PDF-File with the given Template
     *
     * @param templateName Template to use
     * @param content      PDF-File to extract the information of
     * @return Map of FieldType-Names to their extracted Content
     */
    @PostMapping(value = "run", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, String>> extractOnly(@RequestPart(value = "name") final String templateName,
                                                           @RequestPart("content") final byte[] content) {
        final ResponseEntity<Map<String, String>> errorResponse = checkRequest(templateName, content);
        if (errorResponse != null) {
            return errorResponse;
        }


        try {
            final ExtractionTemplate template = this.templateRepository.findTemplateByName(templateName);

            final Map<Field, String> extracted = new CombinedExtractedWU(template.getFields(), content).work(this.tess);
            final Map<String, String> result = FieldUtils.map(extracted);

            LoggerFactory.getLogger(getClass())
                    .trace("Returning OCR result: {}", result);

            return ResponseEntity.ok(result);
        } catch (final Exception e) {
            LoggerFactory.getLogger(getClass())
                    .error("Exception while extracting from the page.", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Extracts the Information from the given PDF-File and inlines the Result in the given Result-Template
     *
     * @param templateName Extraction-Template to Use
     * @param resultName   Result-Template to Use
     * @param content      PDF-File
     * @return Base64 encoded String of the resulting PDF-File
     */
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> extractAndConvert(@RequestPart(value = "templateName") final String templateName,
                                                    @RequestPart("resultName") final String resultName,
                                                    @RequestPart("content") final byte[] content) {
        final ResponseEntity<String> errorResponse = checkRequest(templateName, content);
        if (errorResponse != null) {
            return errorResponse;
        }

        if (!this.resultRepository.existsTemplateByName(resultName)) {
            LoggerFactory.getLogger(getClass())
                    .warn("Unknown TemplateName \"{}\"", resultName);
            return ResponseEntity.notFound().build();
        }

        try {
            final ExtractionTemplate template = this.templateRepository.findTemplateByName(templateName);
            final ResultTemplate result = this.resultRepository.findResultByName(resultName);

            final Map<Field, String> extracted = new CombinedExtractedWU(template.getFields(), content).work(this.tess);
            final byte[] out = new XDocWU(extracted, result.getContent()).work();

            return ResponseEntity.ok().body(Base64.getEncoder().encodeToString(out));
        } catch (final Exception e) {
            LoggerFactory.getLogger(getClass())
                    .error("Exception while converting page.", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private <T> ResponseEntity<T> checkRequest(final String templateName,
                                               final byte[] content) {
        if (!StringUtils.hasText(templateName) || content == null || content.length == 0) {
            LoggerFactory.getLogger(getClass())
                    .warn("Received invalid Request {} : {}", templateName, content != null ? new String(content) : "<null>");
            return ResponseEntity.badRequest().build();
        }
        LoggerFactory.getLogger(getClass())
                .trace("Received Request {}", templateName);

        if (!this.templateRepository.existsTemplateByName(templateName)) {
            LoggerFactory.getLogger(getClass())
                    .warn("Unknown TemplateName \"{}\"", templateName);
            return ResponseEntity.notFound().build();
        }
        return null;
    }
}
