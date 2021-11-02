package de.baleipzig.pdfextraction.backend.rest;

import de.baleipzig.pdfextraction.backend.entities.ResultTemplate;
import de.baleipzig.pdfextraction.backend.repositories.ResultRepository;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping("/rest/results")
public class ResultController {

    private final ResultRepository repo;
    @Value("${server.port}")
    private String port;
    @Value("${server.host}")
    private String host;

    public ResultController(ResultRepository repo) {
        this.repo = repo;
    }

    private static boolean isValidDTO(final String name, final byte[] content) {
        if (!StringUtils.hasText(name)) {
            return false;
        }

        return content != null && content.length > 0;
    }

    /**
     * Loads all Names of all saved Result-Templates in the DB
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
                .map(ResultTemplate::getName)
                .toList();

        LogManager.getLogger(getClass())
                .atTrace()
                .log("Responding with {}", listOfNames);

        return ResponseEntity.ok(listOfNames);
    }

    /**
     * Saves the Result-Template in the DB
     *
     * @param name    Name of the Result-Template
     * @param content Content of the Template
     * @return If the Saving was successful or not via HTTP-Codes
     */
    @PutMapping
    public ResponseEntity<Void> saveTemplate(@RequestPart("name") final String name,
                                             @RequestPart("content") final byte[] content) {
        if (!isValidDTO(name, content)) {
            LogManager.getLogger(getClass())
                    .atError()
                    .log("Received bad Request with a invalid DTO {}", name);
            return ResponseEntity.badRequest().build();
        }

        final ResultTemplate template;
        if (this.repo.existsTemplateByName(name)) {
            template = this.repo.findResultByName(name);
        } else {
            template = new ResultTemplate();
        }

        template.setName(name);
        template.setContent(content);

        final ResultTemplate saved = this.repo.save(template);
        LogManager.getLogger(getClass())
                .atDebug()
                .log("Saved Entity {}", saved);
        return ResponseEntity.created(URI.create("%s:%s/rest/template?name=%s".formatted(this.host, this.port, saved.getName()))).build();
    }
}
