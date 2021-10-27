package de.baleipzig.pdfextraction.backend.rest;

import de.baleipzig.pdfextraction.backend.entities.ExtractionTemplate;
import de.baleipzig.pdfextraction.backend.entities.Field;
import de.baleipzig.pdfextraction.backend.repositories.TemplateRepository;
import de.baleipzig.pdfextraction.backend.util.PDFUtils;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/rest/extraction")
public class ExtractionController {

    private final TemplateRepository repo;

    public ExtractionController(TemplateRepository repo) {
        this.repo = repo;
    }

    @PostMapping(value = "test", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<byte[]> createTestImage(@RequestPart(value = "name") final String templateName, @RequestPart("content") final byte[] content) {
        ResponseEntity<byte[]> errorResponse = checkRequest(templateName, content);
        if (errorResponse != null) {
            return errorResponse;
        }

        final ExtractionTemplate template = this.repo.findTemplateByName(templateName);

        try {
            final RenderedImage image = PDFUtils.toImage(template, content);
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", stream);
            return ResponseEntity.ok().body(stream.toByteArray());
        } catch (final UncheckedIOException | IOException e) {
            LoggerFactory.getLogger(getClass())
                    .error("Exception while converting page.", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "run", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, String>> runAnalysis(@RequestPart(value = "name") final String templateName, @RequestPart("content") final byte[] content) {
        ResponseEntity<Map<String, String>> errorResponse = checkRequest(templateName, content);
        if (errorResponse != null) {
            return errorResponse;
        }

        final ExtractionTemplate template = this.repo.findTemplateByName(templateName);

        final Map<Field, String> extracted = PDFUtils.extract(template, content);
        final Map<String, String> result = new HashMap<>(extracted.size());
        extracted.keySet().forEach(f -> result.put(f.getType().getName(), extracted.get(f)));

        return ResponseEntity.ok(result);
    }

    private <T> ResponseEntity<T> checkRequest(String templateName, byte[] content) {
        if (!StringUtils.hasText(templateName) || content == null || content.length == 0) {
            LoggerFactory.getLogger(getClass())
                    .warn("Received invalid Request {} : {}", templateName, content != null ? new String(content) : "<null>");
            return ResponseEntity.badRequest().build();
        }
        LoggerFactory.getLogger(getClass())
                .trace("Received Request {}", templateName);

        if (!this.repo.existsTemplateByName(templateName)) {
            LoggerFactory.getLogger(getClass())
                    .warn("Unknown TemplateName \"{}\"", templateName);
            return ResponseEntity.notFound().build();
        }
        return null;
    }
}
