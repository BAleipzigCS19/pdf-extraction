package de.baleipzig.pdfextraction.controller;

import de.baleipzig.pdfextraction.client.PDFPreview;
import de.baleipzig.pdfextraction.common.alert.AlertUtils;
import de.baleipzig.pdfextraction.common.controller.ControllerUtils;
import de.baleipzig.pdfextraction.utils.CheckedSupplier;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class ImportController {

    private static final PDFPreview renderer = new PDFPreview();

    @FXML
    public Button continueButton;

    @FXML
    public ComboBox<String> templateComboBox;

    @FXML
    public Button createTemplateButton;

    @FXML
    public Label pageIndex;

    @FXML
    public Button buttonPageBack;

    @FXML
    public Button buttonPageForward;

    @FXML
    public ImageView imageView;

    @FXML
    public Button buttonChooseFile;

    @FXML
    private void continueButtonOnAction() {

        ControllerUtils.switchScene(
                (Stage) this.continueButton.getScene().getWindow(),
                getClass().getResource("/view/ActionView.fxml")
        );
    }

    @FXML
    public void createTemplateButtonOnAction() {

        ControllerUtils.switchScene(
                (Stage) this.continueButton.getScene().getWindow(),
                getClass().getResource("/view/CreateTemplateView.fxml")
        );
    }

    @FXML
    private void initialize() {

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

    private void updatePdfPreview(final CheckedSupplier<Image> image) {
        try {
            final Image toSet = image.get();
            imageView.setImage(toSet);
            pageIndex.setText("Seite: %d/%d".formatted(renderer.getCurrentPage() + 1, renderer.getNumberOfPages() + 1));
        } catch (final Throwable e) {
            LoggerFactory.getLogger(ImportController.class)
                    .atError()
                    .setCause(e)
                    .log("Exception occurred while setting new Image.");

            AlertUtils.showAlert(Alert.AlertType.ERROR, "Fehler", "Ein Fehler ist aufgetreten.", e.getLocalizedMessage());
        }
    }

}
