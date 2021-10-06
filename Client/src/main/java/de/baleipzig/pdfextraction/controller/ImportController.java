package de.baleipzig.pdfextraction.controller;

import de.baleipzig.pdfextraction.common.controller.ControllerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ImportController implements Initializable {

    public Button continueButton;
    public ComboBox templateComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //empty Method
    }

    @FXML
    private void continueButtonOnAction(ActionEvent actionEvent) {

        ControllerUtils.switchScene(
                (Stage) ((Node) actionEvent.getSource()).getScene().getWindow(),
                getClass().getResource("/view/ActionView.fxml")
        );
    }
}
