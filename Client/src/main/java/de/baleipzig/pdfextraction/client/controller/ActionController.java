package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.connector.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.utils.Job;
import de.baleipzig.pdfextraction.client.view.Imports;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

import java.util.Map;

public class ActionController {

    @FXML
    private CheckBox createTerminationCheckBox;

    @FXML
    private Button backToImportButton;

    @Inject
    private TemplateConnector connector;

    @Inject
    private Job job;

    @FXML
    private void runActionButtonOnAction() {
        this.connector.runJob(this.job.getTemplateName(), this.job.getPathToFile())
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .doOnSuccess(v -> Platform.runLater(() -> onSuccess(v)))
                .subscribe();
    }

    private void onSuccess(final Map<String, String> result) {
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : result.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }

        AlertUtils.showAlert(Alert.AlertType.INFORMATION,
                "Erfolgreich",
                "Aktion ausgeführt",
                "Die Aktion wurde erfolgreich ausgeführt.\n\n" + sb);
    }

    @FXML
    private void backToImportButtonOnAction() {
        ControllerUtils.switchScene((Stage) this.backToImportButton.getScene().getWindow(),
                new Imports());
    }
}
