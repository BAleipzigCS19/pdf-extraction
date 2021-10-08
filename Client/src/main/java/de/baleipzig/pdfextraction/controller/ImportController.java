package de.baleipzig.pdfextraction.controller;

import de.baleipzig.pdfextraction.client.PDFPreview;
import de.baleipzig.pdfextraction.common.controller.ControllerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImportController {

    @FXML
    public Button continueButton;

    @FXML
    public ComboBox<String> templateComboBox;

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

    private static Path pdfPath = null;

    @FXML
    private void continueButtonOnAction() {

        ControllerUtils.switchScene(
                (Stage) this.continueButton.getScene().getWindow(),
                getClass().getResource("/view/ActionView.fxml")
        );
    }

    @FXML
    private void initialize() {

        // image view resizeble machen
        AnchorPane parent = (AnchorPane) imageView.getParent();
        imageView.fitWidthProperty().bind(parent.widthProperty());
        imageView.fitHeightProperty().bind(parent.heightProperty());

        if (pdfPath == null){
            pageIndex.setText("Seitenanzahl");
        } else {
            updatePdfPreview(PDFPreview.getCurrentPage());
        }
    }

    public void onClickPageBack(ActionEvent actionEvent) {

        if (PDFPreview.getCurrentPage() > 1 && pdfPath != null){

            int pageNumber = PDFPreview.getCurrentPage() - 1;
            updatePdfPreview(pageNumber);
        }
    }

    public void onClickPageForward(ActionEvent actionEvent) {

        if (PDFPreview.getCurrentPage() < PDFPreview.numberOfPages && pdfPath != null){

            int pageNumber = PDFPreview.getCurrentPage() + 1;
            updatePdfPreview(pageNumber);
        }
    }

    public void onClickChooseFile(ActionEvent actionEvent) {

        // Filechooser
        Stage currentStage = (Stage) this.continueButton.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();

        File selectedFile = fileChooser.showOpenDialog(currentStage);
        pdfPath = Paths.get(selectedFile.toURI());

        // die aktuelle Seitenzahl soll resetet werden wenn eine neue Datei geladen wird
        PDFPreview.setCurrentPage(1);
        updatePdfPreview(PDFPreview.getCurrentPage());
    }

    private void loadImage(){
        Image previewImage = PDFPreview.createPreviewImage(PDFPreview.getCurrentPage() - 1, pdfPath);
        imageView.setImage(previewImage);
    }

    private void updatePdfPreview(int pageNumber){

        PDFPreview.setCurrentPage(pageNumber);
        pageIndex.setText("Seite: " + PDFPreview.getCurrentPage());
        loadImage();
    }

}
