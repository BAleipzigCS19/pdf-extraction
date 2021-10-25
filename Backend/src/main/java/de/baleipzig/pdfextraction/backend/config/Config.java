package de.baleipzig.pdfextraction.backend.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class Config {

    private static Path PATH_TO_CONFIG;
    private final String tessPath;
    private final String tessLanguage;
    public Config()
            throws IOException {
        final Properties properties = getProperties();

        this.tessPath = Objects.requireNonNull(properties.getProperty("tess.path"), "Tesseract Path not set");
        this.tessLanguage = Objects.requireNonNull(properties.getProperty("tess.language"), "Tesseract Language not set");
    }

    public static void setPathToConfig(Path path) {
        PATH_TO_CONFIG = path;
    }

    public String getTessPath() {
        return tessPath;
    }

    public String getTessLanguage() {
        return tessLanguage;
    }

    private Properties getProperties()
            throws IOException {
        final Properties props = new Properties();
        try (InputStream stream = Files.newInputStream(PATH_TO_CONFIG)) {
            props.load(stream);
        }
        return props;
    }
}
