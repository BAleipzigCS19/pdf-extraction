package de.baleipzig.pdfextraction.common.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ControllerUtils {

    private ControllerUtils() {}

    public static void switchScene(final Stage current, final URL linkToNewScene) {

        try {
            final Parent root = FXMLLoader.load(linkToNewScene);
            final Scene scene = new Scene(root);
            current.setScene(scene);
            current.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
