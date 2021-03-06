package de.baleipzig.pdfextraction.client;

import de.baleipzig.pdfextraction.client.config.Config;
import de.baleipzig.pdfextraction.client.utils.SceneHandler;
import de.baleipzig.pdfextraction.client.utils.injector.Injector;
import de.baleipzig.pdfextraction.client.view.Imports;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

public class Launcher extends Application {


    public static void main(String[] args) {
        final String pathToConfig = Arrays.stream(args)
                .filter(s -> s.startsWith("pathToConfig"))
                .findFirst()
                .orElse("./config.properties");
        Config.setPathToConfig(Path.of(pathToConfig));

        Locale.setDefault(Locale.GERMAN);
        launch(args);
    }

    @Override
    public void start(final Stage stage) {
        stage.setTitle("pdf-extraction");
        SceneHandler sceneHandler = Injector.createInstance(SceneHandler.class);
        sceneHandler.switchScene(stage, new Imports());
    }
}
