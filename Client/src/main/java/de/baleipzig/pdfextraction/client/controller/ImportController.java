package de.baleipzig.pdfextraction.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.client.connector.api.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.*;
import de.baleipzig.pdfextraction.client.view.Actions;
import de.baleipzig.pdfextraction.client.workunits.DrawRectangleWU;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.*;

public class ImportController extends Controller implements Initializable, PropertyChangeListener {

    @FXML
    public MenuBar menuBar;
    @FXML
    public JFXButton showTemplateButton;
    @FXML
    public AnchorPane pdfAnchor;
    @FXML
    private Button continueButton;
    @FXML
    private JFXComboBox<Label> templateComboBox;
    @Inject
    private TemplateConnector connector;
    @Inject
    private Job job;
    @Inject
    private PDFRenderer renderer;
    @FXML
    private MenuBarController menuBarController;
    @FXML
    private PdfPreviewController pdfPreviewController;

    private boolean showTemplateToggle = false;

    @FXML
    private void continueButtonOnAction() {

        switchScene((Stage) this.continueButton.getScene().getWindow(),
                new Actions());
    }

    private void setChoosenTemplate(Optional<Label> choosenTemplate) {

        if (choosenTemplate.isEmpty()) {
            //Intentionally not checking if something is set in the job
            AlertUtils.showErrorAlert(getResource("alertChooseTemplate"));

        }

        if (this.job.getPathToFile() == null) {
            AlertUtils.showErrorAlert(getResource("alertChoosePDF"));

        }

        choosenTemplate.map(Labeled::getText).ifPresent(this.job::setTemplateName);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.connector.getAllNames()
                .collectList()
                .doOnError(err -> LoggerFactory.getLogger(ImportController.class)
                        .error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(name -> Platform.runLater(() -> onRequestCompleted(name)));

        EventUtils.chainAfterOnAction(this.menuBarController.chooseFile, this.pdfPreviewController::updatePdfPreview);

        changeFocusOnControlParent(menuBar);
        checkShowTemplateButtonCondition();
        job.addPropertyChangeListener(this);
        templateComboBox.valueProperty().addListener((observable, oldValue, newValue) -> setChoosenTemplate(Optional.ofNullable(newValue)));
    }

    private Optional<Label> getLabelMatching(String templateName, List<Label> comboBoxItems) {

        return comboBoxItems.stream()
                .filter(l -> templateName.equals(l.getText()))
                .findFirst();
    }

    private void onRequestCompleted(final List<String> name) {

        final List<Label> labels = name.stream().map(Label::new).toList();
        this.templateComboBox.getItems().addAll(labels);

        Optional.ofNullable(job.getTemplateName())
                .flatMap(templateName -> getLabelMatching(templateName, labels))
                .ifPresent(l -> templateComboBox.getSelectionModel().select(l));
    }

    public void showTemplateButtonOnAction() {

        if (this.job.getPathToFile() == null) {
            AlertUtils.showErrorAlert(getResource("alertChoosePDF"));
            return;
        }

        if (!showTemplateToggle) {

            String selectedTemplate = this.job.getTemplateName();

            if (selectedTemplate == null) {
                AlertUtils.showErrorAlert(getResource("alertChooseTemplate"));
                return;
            }

            loadTemplate(selectedTemplate);
            this.showTemplateToggle = true;
            this.showTemplateButton.setText(getResource("undoShowTemplateButton"));
        } else {

            removeRectangles();
            this.showTemplateToggle = false;
            this.showTemplateButton.setText(getResource("showTemplateButton"));
        }
    }

    private void removeRectangles() {

        List<Rectangle> addedRectangles = pdfAnchor.getChildren()
                .stream()
                .filter(Rectangle.class::isInstance)
                .map(node -> (Rectangle) node)
                .toList();

        pdfAnchor.getChildren().removeAll(addedRectangles);
    }

    private void loadTemplate(String templateName) {

        this.connector.getForName(templateName)
                .doOnError(err -> LoggerFactory.getLogger(ImportController.class)
                        .error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(templateDTO -> Platform.runLater(() -> drawRectangles(templateDTO)))
        ;
    }

    private void drawRectangles(TemplateDTO templateDTO) {

        DrawRectangleWU drawRectangleWU = new DrawRectangleWU(pdfPreviewController.pdfPreviewImageView,
                templateDTO, renderer.getCurrentPage());

        List<Rectangle> rectangles = drawRectangleWU.work();
        pdfAnchor.getChildren().addAll(rectangles);
    }

    private void checkShowTemplateButtonCondition() {

        if (this.job.getTemplateName() != null && this.job.getPathToFile() != null){
            showTemplateButton.setDisable(false);
            showTemplateButton.getTooltip().setText(getResource("showTemplateButtonTooltipEnabled"));
        } else {
            showTemplateButton.setDisable(true);
            showTemplateButton.getTooltip().setText(getResource("showTemplateButtonTooltipDisabled"));
        }
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        checkShowTemplateButtonCondition();
    }
}
