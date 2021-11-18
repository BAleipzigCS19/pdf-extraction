package de.baleipzig.pdfextraction.client.controller;

import com.jfoenix.controls.JFXComboBox;
import de.baleipzig.pdfextraction.client.connector.api.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.EventUtils;
import de.baleipzig.pdfextraction.client.utils.Job;
import de.baleipzig.pdfextraction.client.view.Actions;
import de.baleipzig.pdfextraction.client.view.CreateTemplate;
import de.baleipzig.pdfextraction.client.view.TemplateOverview;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ImportController extends Controller implements Initializable {

    @FXML
    public Button showTemplatesButton;

    @FXML
    public MenuBar menuBar;

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
}
