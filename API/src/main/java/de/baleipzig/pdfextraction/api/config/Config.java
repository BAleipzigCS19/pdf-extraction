package de.baleipzig.pdfextraction.api.config;

import jakarta.inject.Singleton;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

@Singleton
public class Config {

    private static Path PATH_TO_CONFIG;

    public static void setPathToConfig(Path path) {
        PATH_TO_CONFIG = path;
    }

    private final String serverURL;

    public Config()
            throws IOException {
        final Properties props = getProperties();

        this.serverURL = Objects.requireNonNull(props.getProperty("server.url"), "Server URL is missing");
    }


    public String getServerURL() {
        return serverURL;
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
