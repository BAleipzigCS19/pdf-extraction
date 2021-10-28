package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.connector.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.utils.Job;
import de.baleipzig.pdfextraction.client.view.Actions;
import de.baleipzig.pdfextraction.client.view.CreateTemplate;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ImportController implements Initializable {

    @FXML
    public MenuBar menuBar;

    @FXML
    private Button continueButton;

    @FXML
    private ComboBox<String> templateComboBox;

    @Inject
    private TemplateConnector connector;

    @Inject
    private Job job;

    @FXML
    private MenuBarController menuBarController;

    @FXML
    private PdfPreviewController pdfPreviewController;

    @FXML
    private void continueButtonOnAction() {
        final Optional<String> chosenValue = Optional.ofNullable(this.templateComboBox.getValue());
        if (chosenValue.isEmpty()) {
            //Intentionally not checking if something is set in the job
            AlertUtils.showErrorAlert("Bitte wählen sie erst eine Vorlage aus.");
            return;
        }

        if (this.job.getPathToFile() == null) {
            AlertUtils.showErrorAlert("Bitte wählen sie zuerst eine PDF Datei aus.");
            return;
        }

        chosenValue.ifPresent(this.job::setTemplateName);

        ControllerUtils.switchScene((Stage) this.continueButton.getScene().getWindow(),
                new Actions());
    }

    @FXML
    private void createTemplateButtonOnAction() {

        ControllerUtils.switchScene((Stage) this.continueButton.getScene().getWindow(),
                new CreateTemplate());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.connector.getAllNames()
                .doOnError(err -> LoggerFactory.getLogger(ImportController.class)
                        .error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(this::onRequestCompleted);

        final EventHandler<ActionEvent> chooseFileMethod = this.menuBarController.chooseFile.getOnAction();
        this.menuBarController.chooseFile.setOnAction(event -> {
            chooseFileMethod.handle(event);
            this.pdfPreviewController.updatePdfPreview();
        });

    }

    private void onRequestCompleted(final String name) {
        this.templateComboBox.getItems().add(name);
    }
}
