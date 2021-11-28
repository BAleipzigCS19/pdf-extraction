package de.baleipzig.pdfextraction.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.api.fields.FieldType;
import de.baleipzig.pdfextraction.client.connector.api.ExtractionConnector;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.client.connector.api.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.*;
import de.baleipzig.pdfextraction.client.view.Actions;
import de.baleipzig.pdfextraction.client.workunits.DrawRectangleWU;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.*;

public class ImportController extends Controller implements Initializable {

    @FXML
    public MenuBar menuBar;

    @FXML
    public VBox dataContent;

    @FXML
    public ProgressIndicator progress;

    @FXML
    public Label collectedDataHeader;

    @FXML
    public JFXButton showTemplateButton;

    @FXML
    public AnchorPane pdfAnchor;

    @FXML
    public GridPane boxesLegend;

    @FXML
    private Button continueButton;

    @FXML
    private JFXComboBox<Label> templateComboBox;

    @Inject
    private TemplateConnector templateConnector;

    @Inject
    private ExtractionConnector extractionConnector;

    @Inject
    private Job job;

    @FXML
    private MenuBarController menuBarController;

    @FXML
    private PdfPreviewController pdfPreviewController;

    @Inject
    private TemplateConnector connector;

    @Inject
    private Job job;

    @Inject
    private PDFRenderer renderer;

    private boolean isRectangleVisible = false;

    @FXML
    private void continueButtonOnAction() {

        if (this.job.getPathToFile() == null) {
            AlertUtils.showErrorAlert(getResource("alertChoosePDF"));
            return;
        }
        if (this.job.getTemplateName() == null) {
            AlertUtils.showErrorAlert((getResource("alertChooseTemplate")));
            return;
        }

        switchScene((Stage) this.continueButton.getScene().getWindow(), new Actions());

    }

    private void setChosenTemplate(Label template) {

        final Optional<Label> chosenTemplate = Optional.ofNullable(template);

        if (chosenTemplate.isEmpty()) {
            //Intentionally not checking if something is set in the job
            AlertUtils.showErrorAlert(getResource("alertChooseTemplate"));
        }

        if (pdfAnchor.getChildren().stream().anyMatch(Rectangle.class::isInstance)) {
            // toggle the Show Template button off, when a new Template is chosen
            showTemplateButtonOnAction();
        }

        chosenTemplate.map(Labeled::getText).ifPresent(this.job::setTemplateName);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.templateConnector.getAllNames()
                .collectList()
                .doOnError(err -> LoggerFactory.getLogger(ImportController.class)
                        .error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(name -> Platform.runLater(() -> onRequestCompleted(name)));

        EventUtils.chainAfterOnAction(this.menuBarController.chooseFile, this.pdfPreviewController::updatePdfPreview);
        EventUtils.chainAfterOnAction(this.pdfPreviewController.pageBackButton, event -> this.updateDrawnBoxes());
        EventUtils.chainAfterOnAction(this.pdfPreviewController.pageForwardButton, event -> this.updateDrawnBoxes());

        changeFocusOnControlParent(menuBar);
        checkShowTemplateButtonCondition();
        job.addPropertyChangeListener(evt -> checkShowTemplateButtonCondition());
        templateComboBox.valueProperty().addListener((observable, oldValue, newValue) -> setChosenTemplate(newValue));
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

    public void selectTemplateOnAction() {

        dataContent.getChildren().clear();
        progress.setVisible(true);
        collectedDataHeader.setVisible(false);

        this.extractionConnector.extractOnly(this.templateComboBox.getValue().getText(), this.job.getPathToFile())
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .doOnSuccess(v -> Platform.runLater(() -> onSuccess(v)))
                .subscribe();
    }

    private void onSuccess(Map<String, String> results) {

        progress.setVisible(false);
        collectedDataHeader.setVisible(true);

        for (Map.Entry<String, String> entry : results.entrySet()) {
            Label labelKey = new Label(entry.getKey());
            labelKey.getStyleClass().add("text-data-key");
            labelKey.setPadding(new Insets(10, 0, 0, 0));
            Label labelValue = new Label(entry.getValue());
            labelValue.getStyleClass().add("text-data-value");
            dataContent.getChildren().addAll(labelKey, labelValue);
        }
    }

    public void showTemplateButtonOnAction() {

        if (this.job.getPathToFile() == null) {
            AlertUtils.showErrorAlert(getResource("alertChoosePDF"));
            return;
        }

        if (!this.isRectangleVisible) {

            String selectedTemplate = this.job.getTemplateName();

            if (selectedTemplate == null) {
                AlertUtils.showErrorAlert(getResource("alertChooseTemplate"));
                return;
            }

            loadTemplate(selectedTemplate);
            this.isRectangleVisible = true;
            this.showTemplateButton.setText(getResource("undoShowTemplateButton"));
        } else {

            removeRectangles();
            this.isRectangleVisible = false;
            this.showTemplateButton.setText(getResource("showTemplateButton"));
        }
    }

    private void removeRectangles() {

        List<Rectangle> addedRectangles = pdfAnchor.getChildren()
                .stream()
                .filter(Rectangle.class::isInstance)
                .map(Rectangle.class::cast)
                .toList();

        while (boxesLegend.getRowCount() > 1) {
            boxesLegend.getChildren().remove(0);
        }
        pdfAnchor.getChildren().removeAll(addedRectangles);
    }

    private void loadTemplate(String templateName) {

        this.connector.getForName(templateName)
                .doOnError(err -> LoggerFactory.getLogger(ImportController.class)
                        .error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(templateDTO -> Platform.runLater(() -> getBoxes(templateDTO)));
    }

    private void getBoxes(TemplateDTO templateDTO) {

        DrawRectangleWU drawRectangleWU = new DrawRectangleWU(pdfPreviewController.pdfPreviewImageView, templateDTO);

        Set<Box> boxes = drawRectangleWU.work();

        for (Box box : boxes) {
            if (box.page() == renderer.getCurrentPage()) {
                generateBoxInformation(box.color(), box.type());
                pdfAnchor.getChildren().add(box.place());
            }
        }
    }

    private void generateBoxInformation(Paint color, FieldType fieldType) {

        final int row = this.boxesLegend.getRowCount();
        final Rectangle colorDot = new Rectangle(20, 20, color);
        final Label label = new Label(fieldType.getName());
        this.boxesLegend.addRow(row, colorDot, label);
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

    private void updateDrawnBoxes() {
        if (isRectangleVisible) {

            removeRectangles();
            String selectedTemplate = this.job.getTemplateName();

            if (selectedTemplate == null) {
                AlertUtils.showErrorAlert(getResource("alertChooseTemplate"));
                return;
            }

            loadTemplate(selectedTemplate);
        }
    }

}
