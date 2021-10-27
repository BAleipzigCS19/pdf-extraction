package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.view.Imports;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

public class ActionController {

    @FXML
    private CheckBox createTerminationCheckBox;

    @FXML
    private Button runActionButton;

    @FXML
    private Button backToImportButton;

    @FXML
    private void runActionButtonOnAction() {

        AlertUtils.showAlert(Alert.AlertType.INFORMATION,
                "Erfolgreich",
                "Aktion ausgeführt",
                "Die Aktion wurde erfolgreich ausgeführt");
    }

    @FXML
    private void backToImportButtonOnAction() {
        ControllerUtils.switchScene((Stage) this.backToImportButton.getScene().getWindow(), new Imports());
    }
}
