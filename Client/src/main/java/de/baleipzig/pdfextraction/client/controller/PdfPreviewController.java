package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.Job;
import de.baleipzig.pdfextraction.client.utils.PDFRenderer;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public class PdfPreviewController implements Initializable {

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

    @Inject
    private PDFRenderer renderer;

    @Inject
    private Job job;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // image view resizeble machen
        AnchorPane parentAnchorPane = (AnchorPane) pdfPreviewImageView.getParent();
        pdfPreviewImageView.fitWidthProperty().bind(parentAnchorPane.widthProperty());
        pdfPreviewImageView.fitHeightProperty().bind(parentAnchorPane.heightProperty());

        if (this.renderer.hasPreview()) {
            loadPdfPreview(this.renderer::getCurrentPreview);
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
                    AlertUtils.showErrorAlert("Es kann nur eine PDF-Datei analysiert werden.");
                    return;
                }

                final File first = files.get(0);
                final boolean isCorrectFormat = first.getName().toLowerCase().endsWith(".pdf");
                if (!isCorrectFormat) {
                    AlertUtils.showErrorAlert("Es werden nur PDF-Dateien unterstützt.");
                    return;
                }

                final Path pathToFile = first.toPath();
                this.job.setPathToFile(pathToFile);
                this.renderer.setPdfPath(pathToFile);
                loadPdfPreview(this.renderer::getCurrentPreview);
            } finally {
                event.setDropCompleted(true);
                event.consume();
            }
        });
    }

    @FXML
    public void onClickPageBack() {

        if (this.renderer.hasPreviousPage()) {
            loadPdfPreview(this.renderer::getPreviousPreview);
        }
    }

    @FXML
    public void onClickPageForward() {

        if (this.renderer.hasNextPage()) {
            loadPdfPreview(this.renderer::getNextPreview);
        }
    }

    public void updatePdfPreview() {
        // der restliche code wird in MenuBarController ausgeführt
        loadPdfPreview(this.renderer::getCurrentPreview);
    }

    private void loadPdfPreview(final Supplier<Image> image) {
        try {
            pdfPreviewImageView.setImage(image.get());
            pageIndexLabel.setText("Seite: %d/%d".formatted(this.renderer.getCurrentPage() + 1, this.renderer.getNumberOfPages()));
        } catch (final UncheckedIOException | IllegalStateException e) {
            LoggerFactory.getLogger(ImportController.class)
                    .atError()
                    .setCause(e)
                    .log("Exception occurred while setting new Image.");

            AlertUtils.showErrorAlert(e);
        }
    }
}
