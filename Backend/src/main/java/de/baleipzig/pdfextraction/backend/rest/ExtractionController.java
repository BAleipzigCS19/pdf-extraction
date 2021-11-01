package de.baleipzig.pdfextraction.backend.rest;

import de.baleipzig.pdfextraction.api.fields.FieldType;
import de.baleipzig.pdfextraction.backend.entities.ExtractionTemplate;
import de.baleipzig.pdfextraction.backend.entities.Field;
import de.baleipzig.pdfextraction.backend.entities.ResultTemplate;
import de.baleipzig.pdfextraction.backend.repositories.ResultRepository;
import de.baleipzig.pdfextraction.backend.repositories.TemplateRepository;
import de.baleipzig.pdfextraction.backend.tesseract.Tess;
import de.baleipzig.pdfextraction.backend.util.PDFUtils;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.core.document.DocumentKind;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@RestController
@RequestMapping("/rest/extraction")
public class ExtractionController {

    private final TemplateRepository templateRepository;
    private final ResultRepository resultRepository;
    private final Tess tess;

    public ExtractionController(TemplateRepository repo, ResultRepository resultRepository, Tess tess) {
        this.templateRepository = repo;
        this.resultRepository = resultRepository;
        this.tess = tess;
    }

    @PostMapping(value = "test", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> createTestImage(@RequestPart(value = "name") final String templateName, @RequestPart("content") final byte[] content) {
        ResponseEntity<String> errorResponse = checkRequest(templateName, content);
        if (errorResponse != null) {
            return errorResponse;
        }

        final ExtractionTemplate template = this.templateRepository.findTemplateByName(templateName);

        try {
            final RenderedImage image = PDFUtils.toImage(template, content);
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", stream);

            return ResponseEntity.ok().body(Base64.getEncoder().encodeToString(stream.toByteArray()));
        } catch (final UncheckedIOException | IOException e) {
            LoggerFactory.getLogger(getClass())
                    .error("Exception while converting page.", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "run", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, String>> extractOnly(@RequestPart(value = "name") final String templateName, @RequestPart("content") final byte[] content) {
        ResponseEntity<Map<String, String>> errorResponse = checkRequest(templateName, content);
        if (errorResponse != null) {
            return errorResponse;
        }

        final ExtractionTemplate template = this.templateRepository.findTemplateByName(templateName);

        final Map<String, String> result = extractFields(template.getFields(), content);

        LoggerFactory.getLogger(getClass())
                .trace("Returning OCR result: {}", result);

        return ResponseEntity.ok(result);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> extractAndConvert(@RequestPart(value = "templateName") final String templateName,
                                                    @RequestPart("content") final byte[] content,
                                                    @RequestPart("resultName") final String resultName) {
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
            final byte[] out = analyse(content, resultName, template);

            return ResponseEntity.ok().body(Base64.getEncoder().encodeToString(out));
        } catch (final Exception e) {
            LoggerFactory.getLogger(getClass())
                    .error("Exception while converting page.", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private byte[] analyse(byte[] content, String resultName, ExtractionTemplate template)
            throws IOException, XDocReportException {
        final Map<String, String> extracted = extractFields(template.getFields(), content);
        extracted.put("Datum", DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDateTime.now()));

        replaceInvalidKeys(extracted, "-", "");
        replaceInvalidKeys(extracted, "ä", "ae");

        LoggerFactory.getLogger(getClass())
                .trace("Extracted: {}", extracted);

        final ResultTemplate result = this.resultRepository.findResultByName(resultName);

        return inlineResult(extracted, result.getContent());
    }

    private void replaceInvalidKeys(Map<String, String> extracted, final String invalidString, final String replacement) {
        final List<String> invalidKeys = extracted.keySet()
                .stream()
                .filter(k -> k.contains(invalidString))
                .toList();

        for (final String invalidKey : invalidKeys) {
            final String value = extracted.remove(invalidKey);
            extracted.put(invalidKey.replace(invalidString, replacement), value);
        }
    }

    private static byte[] inlineResult(final Map<String, String> extracted, final byte[] document)
            throws IOException, XDocReportException {
        final IXDocReport report = XDocReportRegistry.getRegistry().loadReport(new ByteArrayInputStream(document), TemplateEngineKind.Velocity);
        final IContext context = report.createContext();
        extracted.forEach(context::put);

        final Options options = Options.getFrom(DocumentKind.ODT).to(ConverterTypeTo.PDF);
        final ByteArrayOutputStream processed = new ByteArrayOutputStream(document.length);
        report.convert(context, options, processed);

        return processed.toByteArray();
    }

    private Map<String, String> extractFields(final Collection<Field> fields, final byte[] content) {
        final Map<Field, String> extracted = PDFUtils.extract(fields, content);
        final Map<String, String> result = new HashMap<>(extracted.size());

        final Collection<Field> emptyFields = new ArrayList<>(fields.size());
        for (final Map.Entry<Field, String> entry : extracted.entrySet()) {
            final Field field = entry.getKey();
            String value = entry.getValue();
            if (!StringUtils.hasText(value)) {
                emptyFields.add(field);
            } else {
                result.put(field.getType().getName(), value.trim());
            }
        }

        if (!emptyFields.isEmpty()) {
            LoggerFactory.getLogger(TemplateController.class)
                    .debug("Could not extract Text with PDFBox for fields {}", emptyFields.stream().map(Field::getType).map(FieldType::getName).toList());

            final Map<Field, String> tessResult = this.tess.doBatchOCR(emptyFields, content);
            tessResult.forEach((k, v) -> result.put(k.getType().getName(), v.trim()));
        }

        final List<String> keyWithNoValue = result.entrySet()
                .stream()
                .filter(e -> !StringUtils.hasText(e.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        if (!keyWithNoValue.isEmpty()) {
            LoggerFactory.getLogger(getClass())
                    .warn("Could not find extract any Text for Fields: {}", keyWithNoValue);
        }

        return result;
    }

    private <T> ResponseEntity<T> checkRequest(String templateName, byte[] content) {
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
