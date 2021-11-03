package de.baleipzig.pdfextraction.client.controller;

import com.jfoenix.controls.JFXComboBox;
import de.baleipzig.pdfextraction.client.connector.api.ExtractionConnector;
import de.baleipzig.pdfextraction.client.connector.api.ResultConnector;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.ResourceBundle;

public class ActionController implements Initializable {

    @FXML
    public MenuBar menuBar;

    @FXML
    public JFXComboBox<String> resultCombobox;

    @FXML
    private CheckBox createTerminationCheckBox;

    @FXML
    private CheckBox createTestImage;

    @FXML
    private Button backToImportButton;

    @Inject
    private ExtractionConnector extractionConnector;

    @Inject
    private ResultConnector resultConnector;

    @Inject
    private Job job;

    @FXML
    private void runActionButtonOnAction() {
        if (this.createTestImage.isSelected()) {
            this.extractionConnector.createTestImage(this.job.getTemplateName(), this.job.getPathToFile())
                    .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                    .doOnSuccess(v -> Platform.runLater(() -> onTestImage(v)))
                    .subscribe();
        }

        if (this.createTerminationCheckBox.isSelected()) {
            final String resultName = this.resultCombobox.getSelectionModel().getSelectedItem();
            if (!StringUtils.hasText(resultName)) {
                AlertUtils.showErrorAlert("Bitte wählen sie eine Vorlage aus.");
                return;
            }


            this.extractionConnector.runJob(this.job.getTemplateName(), this.job.getPathToFile(), resultName)
                    .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                    .doOnSuccess(v -> Platform.runLater(() -> onSuccess(v)))
                    .subscribe();
        }

        /*
        this.extractionConnector.extractOnly(this.job.getTemplateName(), this.job.getPathToFile())
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .doOnSuccess(v -> Platform.runLater(() -> onSuccess(v)))
                .subscribe();
         */
    }

    private void onSuccess(final byte[] pdfBytes) {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle("Ergebnis Speichern unter");
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        final File toSave = chooser.showSaveDialog(this.menuBar.getScene().getWindow());
        if (toSave == null) {
            return;
        }

        try {
            Files.write(toSave.toPath(), pdfBytes);
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, null, null, "Datei gespeichert");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        this.resultConnector.getAllNames()
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(name -> this.resultCombobox.getItems().add(name));

        this.resultCombobox.setVisible(false);
        this.createTerminationCheckBox.setOnAction(ev -> this.resultCombobox.setVisible(!this.resultCombobox.isVisible()));
    }
}
