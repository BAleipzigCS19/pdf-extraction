package de.baleipzig.pdfextraction.common.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerUtils {

    public static void switchScene(ActionEvent actionEvent, Object context, String pathToSwitch) {

        try {
            Parent root = FXMLLoader.load(context.getClass().getResource(pathToSwitch));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
