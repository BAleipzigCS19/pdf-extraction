package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.connector.api.ExtractionConnector;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.utils.Job;
import de.baleipzig.pdfextraction.client.view.Imports;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ActionController implements Initializable {

    @FXML
    public MenuBar menuBar;

    @FXML
    private CheckBox createTerminationCheckBox;

    @FXML
    private CheckBox createTestImage;

    @FXML
    private Button backToImportButton;

    @Inject
    private ExtractionConnector connector;

    @Inject
    private Job job;

    @FXML
    private void runActionButtonOnAction() {
        if (this.createTestImage.isSelected()) {
            this.connector.createTestImage(this.job.getTemplateName(), this.job.getPathToFile())
                    .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                    .doOnSuccess(v -> Platform.runLater(() -> onTestImage(v)))
                    .subscribe();
        }


        this.connector.runJob(this.job.getTemplateName(), this.job.getPathToFile())
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .doOnSuccess(v -> Platform.runLater(() -> onSuccess(v)))
                .subscribe();
    }

    private void onTestImage(final Image image) {
        final Dialog<Void> dialog = new Dialog<>();
        final DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
        dialogPane.getChildren().add(new ImageView(image));
        dialogPane.setMinSize(image.getWidth() + 100, image.getHeight());
        dialog.show();
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerUtils.changeFocusOnControlParent(menuBar);
    }
}
