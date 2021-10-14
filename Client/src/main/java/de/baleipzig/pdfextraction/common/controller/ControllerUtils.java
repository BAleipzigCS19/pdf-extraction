package de.baleipzig.pdfextraction.common.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class ControllerUtils {

    private ControllerUtils() {
    }

    public static void switchScene(final Stage current, final URL linkToNewScene) {

        try {
            final Parent root = FXMLLoader.load(linkToNewScene);
            final Scene scene = new Scene(root);
            current.setScene(scene);
            current.show();
        } catch (IOException e) {
            LoggerFactory.getLogger(ControllerUtils.class)
                    .atError()
                    .addArgument(current)
                    .addArgument(linkToNewScene)
                    .setCause(e)
                    .log("Exception occurred while switching Scene's from {} to {}");
        }
    }
}
