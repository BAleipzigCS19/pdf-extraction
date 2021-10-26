package de.baleipzig.pdfextraction.backend.rest;

import de.baleipzig.pdfextraction.api.dto.FieldDTO;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.backend.entities.Field;
import de.baleipzig.pdfextraction.backend.entities.Template;
import de.baleipzig.pdfextraction.backend.repositories.TemplateRepository;
import de.baleipzig.pdfextraction.backend.util.PDFUtils;
import org.apache.logging.log4j.LogManager;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/rest/")
public class TemplateController {

    @Value("${server.port}")
    private String port;

    @Value("${server.host}")
    private String host;

    private final TemplateRepository repo;

    public TemplateController(TemplateRepository repo) {
        this.repo = repo;
    }

    @GetMapping(path = "template/names")
    public ResponseEntity<Collection<String>> getAllNames() {
        LogManager.getLogger(TemplateController.class)
                .atDebug()
                .log("Received Request for all Names.");

        final Collection<String> listOfNames = this.repo.findAll()
                .stream()
                .map(Template::getName)
                .toList();

        LogManager.getLogger(TemplateController.class)
                .atTrace()
                .log("Responding with {}", listOfNames);

        return ResponseEntity.ok(listOfNames);
    }

    @GetMapping(path = "template")
    public ResponseEntity<TemplateDTO> getForName(@RequestParam(name = "name") final String templateName) {
        if (templateName.isBlank()) {
            LogManager.getLogger(TemplateController.class)
                    .atError()
                    .log("Received bad Request with empty TemplateName");
            return ResponseEntity.badRequest().build();
        }

        LogManager.getLogger(TemplateController.class)
                .atDebug()
                .log("Received Request for template with name \"{}\"", templateName);

        if (!this.repo.existsTemplateByName(templateName)) {
            LogManager.getLogger(TemplateController.class)
                    .atError()
                    .log("Could not find template with name \"{}\"", templateName);
            return ResponseEntity.notFound().build();
        }

        try {
            final Template savedEntity = this.repo.findTemplateByName(templateName);
            LogManager.getLogger(TemplateController.class)
                    .atDebug()
                    .log("Found Template for name \"{}\":\"{}\"", templateName, savedEntity);
            return ResponseEntity.ok(new TemplateDTO(savedEntity.getName(), savedEntity.getConsumer(), mapToFieldDTO(savedEntity.getFields())));
        } catch (final IllegalArgumentException e) {
            LogManager.getLogger(TemplateController.class)
                    .error("Exception occurred while searching for name {}", templateName, e);

            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "template")
    public ResponseEntity<Void> saveTemplate(@RequestBody final TemplateDTO toSave) {
        if (!isValidDTO(toSave)) {
            LogManager.getLogger(TemplateController.class)
                    .atError()
                    .log("Received bad Request with a invalid Template {}", toSave);
            return ResponseEntity.badRequest().build();
        }

        final Template template;
        if (this.repo.existsTemplateByName(toSave.getName())) {
            template = this.repo.findTemplateByName(toSave.getName());
        } else {
            template = new Template();
        }

        template.setName(toSave.getName());
        template.setConsumer(toSave.getConsumer());
        template.setFields(mapToField(toSave.getFields()));

        final Template saved = this.repo.save(template);
        LogManager.getLogger(TemplateController.class)
                .atDebug()
                .log("Saved Entity {}", saved);
        return ResponseEntity.created(URI.create("%s:%s/rest/template?name=%s".formatted(this.host, this.port, saved.getName()))).build();
    }

    @PostMapping(value = "test", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<byte[]> createTestImage(@RequestPart(value = "name") final String templateName, @RequestPart("content") final byte[] content) {
        if (!StringUtils.hasText(templateName) || content == null || content.length == 0) {
            LoggerFactory.getLogger(TemplateController.class)
                    .warn("Received invalid Request {} : {}", templateName, content != null ? new String(content) : "<null>");
            return ResponseEntity.badRequest().build();
        }
        LoggerFactory.getLogger(TemplateController.class)
                .trace("Received Request {}", templateName);

        if (!this.repo.existsTemplateByName(templateName)) {
            LoggerFactory.getLogger(TemplateController.class)
                    .warn("Unknown TemplateName \"{}\"", templateName);
            return ResponseEntity.notFound().build();
        }

        final Template template = this.repo.findTemplateByName(templateName);

        try {
            final RenderedImage image = PDFUtils.toImage(template, content);
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", stream);
            return ResponseEntity.ok().body(stream.toByteArray());
        } catch (final UncheckedIOException | IOException e) {
            LoggerFactory.getLogger(TemplateController.class)
                    .error("Exception while converting page.", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "runAnalysis", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, String>> runAnalysis(@RequestPart(value = "name") final String templateName, @RequestPart("content") final byte[] content) {
        if (!StringUtils.hasText(templateName) || content == null || content.length == 0) {
            LoggerFactory.getLogger(TemplateController.class)
                    .warn("Received invalid Request {} : {}", templateName, content != null ? new String(content) : "<null>");
            return ResponseEntity.badRequest().build();
        }
        LoggerFactory.getLogger(TemplateController.class)
                .trace("Received Request {}", templateName);

        if (!this.repo.existsTemplateByName(templateName)) {
            LoggerFactory.getLogger(TemplateController.class)
                    .warn("Unknown TemplateName \"{}\"", templateName);
            return ResponseEntity.notFound().build();
        }

        final Template template = this.repo.findTemplateByName(templateName);

        final Map<Field, String> extracted = PDFUtils.extract(template, content);
        final Map<String, String> result = new HashMap<>(extracted.size());
        extracted.keySet().forEach(f -> result.put(f.getType().getName(), extracted.get(f)));

        return ResponseEntity.ok(result);
    }

    private static boolean isValidDTO(final TemplateDTO dto) {
        final boolean areFieldsValid = dto != null && dto.getName() != null && !dto.getName().isBlank()
                && dto.getConsumer() != null && !dto.getConsumer().isBlank()
                && dto.getFields() != null && !dto.getFields().isEmpty();
        if (!areFieldsValid) {
            return false;
        }

        for (final FieldDTO field : dto.getFields()) {
            if (!isFieldDTOValid(field)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isFieldDTOValid(FieldDTO field) {
        if (field.getPage() < 0) {
            return false;
        }

        if (field.getWidthPercentage() < 0 || field.getWidthPercentage() > 1) {
            return false;
        }

        if (field.getHeightPercentage() < 0 || field.getHeightPercentage() > 1) {
            return false;
        }

        if (field.getxPosPercentage() < 0 || field.getxPosPercentage() > 1) {
            return false;
        }

        if (field.getyPosPercentage() < 0 || field.getyPosPercentage() > 1) {
            return false;
        }

        if (field.getxPosPercentage() + field.getWidthPercentage() > 1) {
            return false;
        }

        return field.getyPosPercentage() + field.getHeightPercentage() <= 1;
    }

    private List<FieldDTO> mapToFieldDTO(List<Field> fields) {
        return fields.stream()
                .map(TemplateController::mapToFieldDTO)
                .toList();
    }

    private static FieldDTO mapToFieldDTO(Field f) {
        return new FieldDTO(f.getType(), f.getPage(), f.getxPosPercentage(), f.getyPosPercentage(),
                f.getWidthPercentage(), f.getHeightPercentage());
    }

    private List<Field> mapToField(List<FieldDTO> fields) {
        return fields.stream()
                .map(TemplateController::mapToField)
                .toList();
    }

    private static Field mapToField(final FieldDTO f) {
        return new Field(f.getType(), f.getPage(), f.getxPosPercentage(), f.getyPosPercentage(),
                f.getWidthPercentage(), f.getHeightPercentage());
    }
}
