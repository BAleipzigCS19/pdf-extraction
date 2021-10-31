package de.baleipzig.pdfextraction.backend.rest;

import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.backend.entities.ExtractionTemplate;
import de.baleipzig.pdfextraction.backend.repositories.TemplateRepository;
import de.baleipzig.pdfextraction.backend.util.ValidationUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping(path = "/rest/template")
public class TemplateController {

    private final TemplateRepository repo;

    @Value("${server.port}")
    private String port;
    @Value("${server.host}")
    private String host;

    public TemplateController(TemplateRepository repo) {
        this.repo = repo;
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
     * @return If the Saving was successful or not via HTTP-Codes
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
}
