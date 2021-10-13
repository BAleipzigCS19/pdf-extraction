package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.connector.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.view.ActionView;
import de.baleipzig.pdfextraction.client.view.CreateTemplate;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ImportController implements Initializable {


    @Inject
    private TemplateConnector connector;

    @FXML
    public Button continueButton;

    @FXML
    public ComboBox<String> templateComboBox;

    @FXML
    public Button createTemplateButton;

    @FXML
    private void continueButtonOnAction() {
        ControllerUtils.switchScene((Stage) this.continueButton.getScene().getWindow(),
                new ActionView());
    }

    @FXML
    public void createTemplateButtonOnAction() {

        ControllerUtils.switchScene((Stage) this.continueButton.getScene().getWindow(),
                new CreateTemplate());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.connector.getAllNames()
                .doOnError(err -> LoggerFactory.getLogger(ImportController.class)
                        .error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(
                        () -> AlertUtils.showAlert(Alert.AlertType.ERROR, "Fehler", "",
                                "Es ist ein Fehler bei dem kommunizieren mit dem Server aufgetreten.")))
                .subscribe(this::onRequestCompleted);
    }

    private void onRequestCompleted(final String name) {
        this.templateComboBox.getItems().add(name);
    }
}
