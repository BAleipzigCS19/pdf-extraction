package de.baleipzig.pdfextraction.controller;

import de.baleipzig.pdfextraction.client.PDFPreview;
import de.baleipzig.pdfextraction.common.alert.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public class PdfPreviewIncludeController implements Initializable {

    @FXML
    public Button pageBackButton;

    @FXML
    public Button pageForwardButton;

    @FXML
    public Label pageIndexLabel;

    @FXML
    public ImageView pdfPreviewImageView;

    @FXML
    public Button chooseFileButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // image view resizeble machen
        AnchorPane parentAnchorPane = (AnchorPane) pdfPreviewImageView.getParent();
        pdfPreviewImageView.fitWidthProperty().bind(parentAnchorPane.widthProperty());
        pdfPreviewImageView.fitHeightProperty().bind(parentAnchorPane.heightProperty());

        if (PDFPreview.getInstance().hasPreview()) {
            updatePdfPreview(PDFPreview.getInstance()::getCurrentPreview);
        } else {
            pageIndexLabel.setText("Seitenanzahl");
        }

        parentAnchorPane.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        parentAnchorPane.setOnDragDropped(event -> {
            try {
                Dragboard dragboard = event.getDragboard();
                if (!dragboard.hasFiles()) {
                    return;
                }

                final List<File> files = dragboard.getFiles();
                if (files.size() > 1) {
                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Fehler", null, "Es kann nur eine PDF-Datei analysiert werden.");
                    return;
                }

                final File first = files.get(0);
                final boolean isCorrectFormat = first.getName().toLowerCase().endsWith(".pdf");
                if (!isCorrectFormat) {
                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Fehler", null, "Es werden nur PDF-Dateien unterstützt.");
                    return;
                }

                PDFPreview.getInstance().setPdfPath(first.toPath());
                updatePdfPreview(PDFPreview.getInstance()::getCurrentPreview);
            } finally {
                event.setDropCompleted(true);
                event.consume();
            }
        });
    }

    @FXML
    public void onClickPageBack() {

        if (PDFPreview.getInstance().hasPreviousPage()) {
            updatePdfPreview(PDFPreview.getInstance()::getPreviousPreview);
        }
    }

    @FXML
    public void onClickPageForward() {

        if (PDFPreview.getInstance().hasNextPage()) {
            updatePdfPreview(PDFPreview.getInstance()::getNextPreview);
        }
    }

    @FXML
    public void onClickChooseFile() {
        // Filechooser
        final Stage current = (Stage) this.chooseFileButton.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().setAll(List.of(new FileChooser.ExtensionFilter("PDFs", "*.pdf")));
        final File selectedFile = fileChooser.showOpenDialog(current);
        if (selectedFile == null) {
            //User canceled the dialog
            return;
        }

        final Path pdfPath = selectedFile.toPath();

        PDFPreview.getInstance().setPdfPath(pdfPath);
        // die aktuelle Seitenzahl soll resetet werden wenn eine neue Datei geladen wird
        updatePdfPreview(PDFPreview.getInstance()::getCurrentPreview);
    }

    private void updatePdfPreview(final Supplier<Image> image) {
        try {
            pdfPreviewImageView.setImage(image.get());
            pageIndexLabel.setText("Seite: %d/%d".formatted(PDFPreview.getInstance().getCurrentPage() + 1, PDFPreview.getInstance().getNumberOfPages()));
        } catch (final UncheckedIOException | IllegalStateException e) {
            LoggerFactory.getLogger(ImportController.class)
                    .atError()
                    .setCause(e)
                    .log("Exception occurred while setting new Image.");

            AlertUtils.showAlert(Alert.AlertType.ERROR, "Fehler", "Ein Fehler ist aufgetreten.", e.getLocalizedMessage());
        }
    }
}
