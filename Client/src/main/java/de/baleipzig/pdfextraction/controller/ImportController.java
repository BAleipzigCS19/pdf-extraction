package de.baleipzig.pdfextraction.controller;

import de.baleipzig.pdfextraction.common.controller.ControllerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class ImportController {

    @FXML
    public Button continueButton;

    @FXML
    public ComboBox<String> templateComboBox;
    @FXML
    public Button createTemplateButton;

    @FXML
    private void continueButtonOnAction() {

        ControllerUtils.switchScene(
                (Stage) this.continueButton.getScene().getWindow(),
                getClass().getResource("/view/ActionView.fxml")
        );
    }

    @FXML
    public void createTemplateButtonOnAction() {

        ControllerUtils.switchScene(
                (Stage) this.continueButton.getScene().getWindow(),
                getClass().getResource("/view/CreateTemplateView.fxml")
        );
    }
}
