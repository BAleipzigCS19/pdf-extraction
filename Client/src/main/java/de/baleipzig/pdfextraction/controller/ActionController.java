package de.baleipzig.pdfextraction.controller;

import de.baleipzig.pdfextraction.common.alert.AlertCommon;
import de.baleipzig.pdfextraction.common.controller.ControllerCommon;
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

        AlertCommon.ShowAlert(Alert.AlertType.INFORMATION, "Erfolgreich", "Aktion ausgeführt", "Die Aktion wurde erfolgreich ausgeführt");
    }

    @FXML
    public void backToImportButtonOnAction(ActionEvent actionEvent) {

        ControllerCommon.switchScene(actionEvent, this, "/view/ImportView.fxml");
    }
}
