package de.baleipzig.pdfextraction.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.baleipzig.pdfextraction.api.dto.FieldDTO;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.client.connector.api.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.EventUtils;
import de.baleipzig.pdfextraction.client.utils.Job;
import de.baleipzig.pdfextraction.client.view.Actions;
import de.baleipzig.pdfextraction.client.view.CreateTemplate;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ImportController extends Controller implements Initializable {

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
    @FXML
    private MenuBarController menuBarController;
    @FXML
    private PdfPreviewController pdfPreviewController;

    @FXML
    private void continueButtonOnAction() {
        final Optional<Label> chosenValue = Optional.ofNullable(this.templateComboBox.getValue());
        if (chosenValue.isEmpty()) {
            //Intentionally not checking if something is set in the job
            AlertUtils.showErrorAlert(getResource("alertChooseTemplate"));
            return;
        }

        if (this.job.getPathToFile() == null) {
            AlertUtils.showErrorAlert(getResource("alertChoosePDF"));
            return;
        }

        chosenValue.map(Labeled::getText).ifPresent(this.job::setTemplateName);

        switchScene((Stage) this.continueButton.getScene().getWindow(),
                new Actions());
    }

    @FXML
    private void createTemplateButtonOnAction() {

        switchScene((Stage) this.continueButton.getScene().getWindow(),
                new CreateTemplate());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        changeFocusOnControlParent(menuBar);

        this.connector.getAllNames()
                .collectList()
                .doOnError(err -> LoggerFactory.getLogger(ImportController.class)
                        .error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(name -> Platform.runLater(() -> onRequestCompleted(name)));

        EventUtils.chainAfterOnAction(this.menuBarController.chooseFile, this.pdfPreviewController::updatePdfPreview);
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

        //TODO: evtl Button Text ändern, also ein Toggle draus machen
        // Prüfen auf welcher Seite sich die Box befindet ?
        // schauen  das eine PDF geladen ist...
        // den Namen der Felder über der Box anzeigen

        Label selectedItem = this.templateComboBox.getValue();

        Optional.ofNullable(selectedItem)
                .map(Label::getText)
                .ifPresent(this::loadTemplate);
    }

    private void loadTemplate(String templateName) {

        this.connector.getForName(templateName)
                .doOnError(err -> LoggerFactory.getLogger(ImportController.class)
                        .error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(templateDTO -> Platform.runLater(() -> drawTemplate(templateDTO)))
        ;
    }

    private void drawTemplate(TemplateDTO templateDTO) {

        List<FieldDTO> boxes = templateDTO.getFields();
        Size size = getSize(pdfPreviewController.pdfPreviewImageView);
        for (FieldDTO field : boxes) {

            Rectangle rectangle = getRectangle(size, field);
            rectangle.setStroke(Color.BLACK);
            rectangle.setFill(Color.TRANSPARENT);
            pdfAnchor.getChildren().add(rectangle);
        }
    }

    private Rectangle getRectangle(Size size, FieldDTO f) {
        final double xPos = (f.getxPosPercentage() * size.width) + AnchorPane.getLeftAnchor(pdfPreviewController.pdfPreviewImageView);
        final double yPos = (f.getyPosPercentage() * size.height) + AnchorPane.getTopAnchor(pdfPreviewController.pdfPreviewImageView);
        final double width = f.getWidthPercentage() * size.width;
        final double height = f.getHeightPercentage() * size.height;
        return new Rectangle(xPos, yPos, width, height);
    }

    private Size getSize(final ImageView imageView) {
        return new Size(imageView.getFitHeight(), imageView.getFitWidth());
    }

    private record Size(double height, double width) {
    }
}
