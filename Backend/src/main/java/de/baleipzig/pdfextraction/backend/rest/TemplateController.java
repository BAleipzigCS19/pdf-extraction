package de.baleipzig.pdfextraction.backend.rest;

import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.backend.entities.Template;
import de.baleipzig.pdfextraction.backend.repositories.TemplateRepository;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<List<String>> getAllNames() {
        final List<String> body = this.repo.findAll()
                .stream()
                .map(Template::getName)
                .toList();

        return ResponseEntity.ok(body);
    }

    @GetMapping(path = "template")
    public ResponseEntity<TemplateDTO> getForName(@RequestParam(name = "name") final String templateName) {
        if (templateName.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (!this.repo.existsTemplateByName(templateName)) {
            return ResponseEntity.notFound().build();
        }

        try {
            final Template body = this.repo.findTemplateByName(templateName);

            return ResponseEntity.ok(new TemplateDTO(body.getName(), body.getContent()));
        } catch (final IllegalArgumentException e) {
            LogManager.getLogger(TemplateController.class)
                    .error("Exception occurred while searching for name %s".formatted(templateName), e);

            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "template")
    public ResponseEntity<Void> saveTemplate(@RequestBody final TemplateDTO toSave) {
        if (!isValidDTO(toSave) || this.repo.existsTemplateByName(toSave.getName())) {
            return ResponseEntity.badRequest().build();
        }

        //Todo: Validate Content

        this.repo.save(new Template(toSave.getName(), toSave.getContent()));

        return ResponseEntity.created(URI.create("%s:%s/rest/template?name=%s".formatted(this.host, this.port, toSave.getName()))).build();
    }

    private static boolean isValidDTO(final TemplateDTO dto) {
        return dto != null && dto.getName() != null && !dto.getName().isBlank() && dto.getContent() != null && !dto.getContent().isBlank();
    }

}
