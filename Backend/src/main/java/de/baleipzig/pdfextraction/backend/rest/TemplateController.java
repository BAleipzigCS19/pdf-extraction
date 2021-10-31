package de.baleipzig.pdfextraction.backend.rest;

import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.api.fields.FieldType;
import de.baleipzig.pdfextraction.backend.entities.Field;
import de.baleipzig.pdfextraction.backend.entities.Template;
import de.baleipzig.pdfextraction.backend.entities.ExtractionTemplate;
import de.baleipzig.pdfextraction.backend.repositories.TemplateRepository;
import de.baleipzig.pdfextraction.backend.util.ValidationUtils;
import de.baleipzig.pdfextraction.backend.tesseract.Tess;
import de.baleipzig.pdfextraction.backend.util.PDFUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.Collection;

@RestController
@RequestMapping(path = "/rest/template")
public class TemplateController {

    private final TemplateRepository repo;
    private final Tess tess;
    @Value("${server.port}")
    private String port;
    @Value("${server.host}")
    private String host;

    public TemplateController(TemplateRepository repo, Tess tess) {
        this.repo = repo;
        this.tess = tess;
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

    /**
     * Loads all Names of all saved Templates in the DB
     *
     * @return A Collection of all names of all saved templates
     */
    @GetMapping(path = "names")
    public ResponseEntity<Collection<String>> getAllNames() {
        LogManager.getLogger(getClass())
                .atDebug()
                .log("Received Request for all Names.");

        final Collection<String> listOfNames = this.repo.findAll()
                .stream()
                .map(ExtractionTemplate::getName)
                .toList();

        LogManager.getLogger(getClass())
                .atTrace()
                .log("Responding with {}", listOfNames);

        return ResponseEntity.ok(listOfNames);
    }

    /**
     * Actually executes an extraction of the PDF with the given template
     *
     * @param templateName Name of the Template to use
     * @param pathToFile   Path to the PDF to use
     * @return Map of fieldtype-names to their extracted Result
     */
    @GetMapping
    public ResponseEntity<TemplateDTO> getForName(@RequestParam(name = "name") final String templateName) {
        if (templateName.isBlank()) {
            LogManager.getLogger(getClass())
                    .atError()
                    .log("Received bad Request with empty TemplateName");
            return ResponseEntity.badRequest().build();
        }

        LogManager.getLogger(getClass())
                .atDebug()
                .log("Received Request for template with name \"{}\"", templateName);

        if (!this.repo.existsTemplateByName(templateName)) {
            LogManager.getLogger(getClass())
                    .atError()
                    .log("Could not find template with name \"{}\"", templateName);
            return ResponseEntity.notFound().build();
        }

        try {
            final ExtractionTemplate savedEntity = this.repo.findTemplateByName(templateName);
            LogManager.getLogger(getClass())
                    .atDebug()
                    .log("Found Template for name \"{}\":\"{}\"", templateName, savedEntity);
            return ResponseEntity.ok(new TemplateDTO(savedEntity.getName(), savedEntity.getConsumer(), ValidationUtils.mapToFieldDTO(savedEntity.getFields())));
        } catch (final IllegalArgumentException e) {
            LogManager.getLogger(getClass())
                    .error("Exception occurred while searching for name {}", templateName, e);

            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Saves the given Template in the Database, overrides any previously saved Template with the same name.
     *
     * @param toSave Template to save
     * @return If the Saving was successfull or not via HTTP-Codes
     */
    @PutMapping
    public ResponseEntity<Void> saveTemplate(@RequestBody final TemplateDTO toSave) {
        if (!ValidationUtils.isValidDTO(toSave)) {
            LogManager.getLogger(getClass())
                    .atError()
                    .log("Received bad Request with a invalid Template {}", toSave);
            return ResponseEntity.badRequest().build();
        }

        final ExtractionTemplate template;
        if (this.repo.existsTemplateByName(toSave.getName())) {
            template = this.repo.findTemplateByName(toSave.getName());
        } else {
            template = new ExtractionTemplate();
        }

        template.setName(toSave.getName());
        template.setConsumer(toSave.getConsumer());
        template.setFields(ValidationUtils.mapToField(toSave.getFields()));

        final ExtractionTemplate saved = this.repo.save(template);
        LogManager.getLogger(getClass())
                .atDebug()
                .log("Saved Entity {}", saved);
        return ResponseEntity.created(URI.create("%s:%s/rest/template?name=%s".formatted(this.host, this.port, saved.getName()))).build();
    }

    @PostMapping(value = "test", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> createTestImage(@RequestPart(value = "name") final String templateName, @RequestPart("content") final byte[] content) {
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

            return ResponseEntity.ok().body(Base64.getEncoder().encodeToString(stream.toByteArray()));
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

        final Map<String, String> result = extractFields(template.getFields(), content);

        LoggerFactory.getLogger(getClass())
                .trace("Returning OCR result: {}", result);

        return ResponseEntity.ok(result);
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
                result.put(field.getType().getName(), value);
            }
        }

        if (!emptyFields.isEmpty()) {
            LoggerFactory.getLogger(TemplateController.class)
                    .debug("Could not extract Text with PDFBox for fields {}", emptyFields.stream().map(Field::getType).map(FieldType::getName).toList());

            final Map<Field, String> tessResult = this.tess.doBatchOCR(emptyFields, content);
            tessResult.forEach((k, v) -> result.put(k.getType().getName(), v));
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

    private static List<FieldDTO> mapToFieldDTO(List<Field> fields) {
        return fields.stream()
                .map(TemplateController::mapToFieldDTO)
                .toList();
    }

    private static FieldDTO mapToFieldDTO(Field f) {
        return new FieldDTO(f.getType(), f.getPage(), f.getxPosPercentage(), f.getyPosPercentage(),
                f.getWidthPercentage(), f.getHeightPercentage());
    }

    private static List<Field> mapToField(List<FieldDTO> fields) {
        return fields.stream()
                .map(TemplateController::mapToField)
                .toList();
    }

    private static Field mapToField(final FieldDTO f) {
        return new Field(f.getType(), f.getPage(), f.getxPosPercentage(), f.getyPosPercentage(),
                f.getWidthPercentage(), f.getHeightPercentage());
    }
}
