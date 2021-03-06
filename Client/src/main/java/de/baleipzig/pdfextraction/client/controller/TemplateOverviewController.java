package de.baleipzig.pdfextraction.client.controller;

import com.jfoenix.controls.JFXButton;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.client.connector.impl.TemplateConnectorImpl;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.view.CreateTemplate;
import de.baleipzig.pdfextraction.client.view.Imports;
import de.baleipzig.pdfextraction.client.view.TemplateItem;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class TemplateOverviewController extends Controller implements Initializable {

    public static final String INIT_TEMPLATE = "INIT_TEMPLATE";
    private static final Image deleteImage = new Image(TemplateOverviewController.class.getResourceAsStream("/de/baleipzig/pdfextraction/client/view/img/delete.png"));
    private static final Image editImage = new Image(TemplateOverviewController.class.getResourceAsStream("/de/baleipzig/pdfextraction/client/view/img/edit.png"));
    private static final Image addImage = new Image(TemplateOverviewController.class.getResourceAsStream("/de/baleipzig/pdfextraction/client/view/img/add.png"));

    @FXML
    public VBox contentPane;

    @FXML
    public JFXButton backButton;

    @FXML
    public JFXButton addTemplateButton;

    @Inject
    private TemplateConnectorImpl connector;

    private final Map<String, String> itemMap = new HashMap<>();
    private final List<VBox> vBoxList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addTemplateButton.setGraphic(new ImageView(addImage));

        this.connector.getAllNames()
                .doOnError(err -> LoggerFactory.getLogger(getClass()).atError().setCause(err).log("Exception while getting all names."))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(this::onRequestForAllNamesCompleted);
    }

    @FXML
    private void backButtonOnAction() {

        switchScene((Stage) this.backButton.getScene().getWindow(), new Imports());
    }

    @FXML
    public void addTemplateButtonOnAction() {

        switchScene((Stage) this.backButton.getScene().getWindow(), new CreateTemplate());
    }

    private void onRequestForAllNamesCompleted(final String name) {

        this.connector.getForName(name)
                .doOnError(err -> LoggerFactory.getLogger(getClass())
                        .error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(templateDTO -> Platform.runLater(() -> onRequestForTemplateDtoCompleted(templateDTO)));
    }

    private void onRequestForTemplateDtoCompleted(TemplateDTO templateDTO) {

        FXMLLoader loader = new FXMLLoader(new TemplateItem().getFXML());
        try {
            Node item = loader.load();
            buildItem(loader, templateDTO, item);

            if (!itemMap.containsValue(templateDTO.getConsumer())) {
                contentPane.getChildren().add(createHeadingWithItem(templateDTO, item));
            } else {
                for (VBox vBox : vBoxList) {
                    if (vBox.getId().equals("#" + templateDTO.getConsumer())) {
                        vBox.getChildren().add(item);
                    }
                }
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass())
                    .atError()
                    .setCause(e)
                    .log("Exception occurred while create item");

            AlertUtils.showErrorAlert(e);
        }

        itemMap.put(templateDTO.getName(), templateDTO.getConsumer());
    }

    private void buildItem(FXMLLoader loader, TemplateDTO templateDTO, Node item) {

        TemplateItemController templateItemController = loader.getController();
        templateItemController.templatename.setText(templateDTO.getName());
        templateItemController.versicherung.setText(templateDTO.getConsumer());
        templateItemController.deleteButton.setGraphic(new ImageView(deleteImage));
        templateItemController.deleteButton.setOnAction(event -> delete(templateDTO));
        templateItemController.editButton.setGraphic(new ImageView(editImage));
        templateItemController.editButton.setOnAction(event -> edit(templateDTO));
        VBox.setMargin(item, new Insets(5));
    }

    private void delete(TemplateDTO templateDTO) {

        this.connector.delete(templateDTO.getName())
                .doOnError(err -> LoggerFactory.getLogger(getClass())
                        .error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .doOnSuccess(a -> Platform.runLater(() -> reloadScene((Stage) contentPane.getScene().getWindow())))
                .doOnSuccess(a -> Platform.runLater(() -> AlertUtils.showAlert(Alert.AlertType.INFORMATION, getResource("successTitle"), null, getResource("deleteContent"))))
                .subscribe();
    }

    private void edit(TemplateDTO templateDTO) {
        final Stage stage = getStage();
        stage.getProperties().put(INIT_TEMPLATE, templateDTO);
        switchScene(stage, new CreateTemplate());
    }

    private Stage getStage() {
        return (Stage) this.backButton.getScene().getWindow();
    }

    private VBox createHeadingWithItem(TemplateDTO templateDTO, Node item) {
        Label heading = new Label(templateDTO.getConsumer());
        heading.getStyleClass().add("header-second");
        VBox.setMargin(heading, new Insets(0, 0, 0, 10));
        VBox vBox = new VBox(heading, item);
        vBox.setId("#" + templateDTO.getConsumer());
        vBoxList.add(vBox);
        return vBox;
    }
}
