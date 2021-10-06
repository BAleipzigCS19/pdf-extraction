package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.client.connector.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.view.ActionView;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class ImportController implements Initializable {

    @Inject
    private TemplateConnector connector;

    @FXML
    public Button continueButton;

    @FXML
    public ComboBox<String> templateComboBox;

    @FXML
    private void continueButtonOnAction() {
        ControllerUtils.switchScene((Stage) this.continueButton.getScene().getWindow(), new ActionView());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.connector
                .getAllNames()
                .doOnError(err -> LoggerFactory.getLogger(ImportController.class).error("Exception while listening for response.", err))
                .onErrorReturn(Collections.emptyList())
                .subscribe(this::onRequestCompleted);
    }

    private void onRequestCompleted(final List<TemplateDTO> response) {
        final List<String> names = response.stream()
                .map(TemplateDTO::getName)
                .toList();

        this.templateComboBox.getItems().setAll(names);
    }
}
