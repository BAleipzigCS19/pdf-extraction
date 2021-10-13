package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.common.controller.ControllerUtils;
import de.baleipzig.pdfextraction.client.utils.PDFPreview;
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

import java.io.File;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;

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

        ControllerUtils.switchScene(
                (Stage) this.continueButton.getScene().getWindow(), new ActionView()););
    }

    @FXML
    public void createTemplateButtonOnAction() {

        ControllerUtils.switchScene(
                (Stage) this.continueButton.getScene().getWindow(),
                getClass().getResource("/view/CreateTemplateView.fxml")
        );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.connector
                .getAllNames()
                .doOnError(err -> LoggerFactory.getLogger(ImportController.class).error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showAlert(Alert.AlertType.ERROR, "Fehler", "", "Es ist ein Fehler bei dem kommunizieren mit dem Server aufgetreten.")))
                .subscribe(this::onRequestCompleted);

        // image view resizeble machen
        AnchorPane parent = (AnchorPane) imageView.getParent();
        imageView.fitWidthProperty().bind(parent.widthProperty());
        imageView.fitHeightProperty().bind(parent.heightProperty());

        if (renderer.hasPreview()) {
            updatePdfPreview(renderer::getCurrentPreview);
        } else {
            pageIndex.setText("Seitenanzahl");
        }
    }

    private void onRequestCompleted(final String name) {
        this.templateComboBox.getItems().add(name);
    }

    @FXML
    public void createTemplateButtonOnAction() {

        ControllerUtils.switchScene(
                (Stage) this.continueButton.getScene().getWindow(),
                new CreateTemplate()
        );
    }

    @FXML
    public void onClickPageBack() {

        if (renderer.hasPreviousPage()) {
            updatePdfPreview(renderer::getPreviousPreview);
        }
    }

    @FXML
    public void onClickPageForward() {

        if (renderer.hasNextPage()) {
            updatePdfPreview(renderer::getNextPreview);
        }
    }

    @FXML
    public void onClickChooseFile() {
        // Filechooser
        final Stage current = (Stage) this.buttonChooseFile.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().setAll(List.of(new FileChooser.ExtensionFilter("PDF's", "*.pdf")));
        final File selectedFile = fileChooser.showOpenDialog(current);
        if (selectedFile == null) {
            //User canceled the dialog
            return;
        }

        final Path pdfPath = selectedFile.toPath();

        renderer.setPdfPath(pdfPath);
        // die aktuelle Seitenzahl soll resetet werden wenn eine neue Datei geladen wird
        updatePdfPreview(renderer::getCurrentPreview);
    }

    private void updatePdfPreview(final Supplier<Image> image) {
        try {
            final Image toSet = image.get();

            imageView.setImage(toSet);
            pageIndex.setText("Seite: %d/%d".formatted(renderer.getCurrentPage() + 1, renderer.getNumberOfPages() + 1));
        } catch (final UncheckedIOException | IllegalStateException e) {
            LoggerFactory.getLogger(ImportController.class)
                    .atError()
                    .setCause(e)
                    .log("Exception occurred while setting new Image.");

            AlertUtils.showAlert(Alert.AlertType.ERROR, "Fehler", "Ein Fehler ist aufgetreten.", e.getLocalizedMessage());
        }
    }

}
