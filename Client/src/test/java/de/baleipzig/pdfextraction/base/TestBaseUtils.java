package de.baleipzig.pdfextraction.base;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class TestBaseUtils {

    public static void start(Stage stage, URL pathToView) {

        try {
            Parent root = FXMLLoader.load(pathToView);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Fehler beim Laden der Scene. Fehlermeldung: " + e.getMessage());
        }
    }
}
