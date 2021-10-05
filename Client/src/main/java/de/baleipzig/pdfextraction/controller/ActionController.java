package de.baleipzig.pdfextraction.controller;

import de.baleipzig.pdfextraction.common.alert.AlertUtils;
import de.baleipzig.pdfextraction.common.controller.ControllerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ActionController implements Initializable {

    public CheckBox createTerminationCheckBox;
    public Button runActionButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //empty Method
    }

    @FXML
    public void runActionButtonOnAction(ActionEvent actionEvent) {

        AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Erfolgreich", "Aktion ausgeführt", "Die Aktion wurde erfolgreich ausgeführt");
    }

    @FXML
    public void backToImportButtonOnAction(ActionEvent actionEvent) {

        ControllerUtils.switchScene(actionEvent, this, "/view/ImportView.fxml");
    }
}
