package de.baleipzig.pdfextraction.client;

import de.baleipzig.pdfextraction.client.config.Config;
import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.view.Imports;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.Arrays;

public class Launcher extends Application {

    public static void main(String[] args) {
        final String pathToConfig = Arrays.stream(args)
                .filter(s -> s.startsWith("pathToConfig"))
                .findFirst()
                .orElse("./config.properties");
        Config.setPathToConfig(Path.of(pathToConfig));

        launch(args);
    }

    @Override
    public void start(final Stage stage) {
        stage.setTitle("pdf-extraction");
        ControllerUtils.switchScene(stage, new Imports());
    }
}
