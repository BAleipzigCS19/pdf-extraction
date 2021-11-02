package de.baleipzig.pdfextraction.backend;

import de.baleipzig.pdfextraction.backend.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.nio.file.Path;
import java.util.Arrays;

@SpringBootApplication
public class Starter extends SpringBootServletInitializer {
    public static void main(String[] args) {
        final String pathToConfig = Arrays.stream(args)
                .filter(s -> s.startsWith("pathToConfig"))
                .findFirst()
                .orElse("./config.properties");
        Config.setPathToConfig(Path.of(pathToConfig));

        SpringApplication.run(Starter.class, args);
    }
}
