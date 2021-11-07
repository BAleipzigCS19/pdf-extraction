package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.client.connector.impl.TemplateConnectorImpl;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.view.Imports;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TemplateOverviewController implements Initializable {

    @FXML
    public VBox contentPane;

    @Inject
    private TemplateConnectorImpl connector;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.connector.getAllNames()
                .doOnError(err -> LoggerFactory.getLogger(ImportController.class)
                        .error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(this::onRequestForAllNamesCompleted);
    }

    private void onRequestForAllNamesCompleted(final String name) {

        this.connector.getForName(name)
                .doOnError(err -> LoggerFactory.getLogger(ImportController.class)
                        .error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(this::onRequestForTemplateDtoCompleted);
    }

    private void onRequestForTemplateDtoCompleted(TemplateDTO templateDTO) {

        Platform.runLater(() -> {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/baleipzig/pdfextraction/client/view/TemplateItem.fxml"));
            try {
                Node node = loader.load();
                TemplateItemController templateItemController = loader.getController();
                templateItemController.templatename.setText(templateDTO.getName());
                templateItemController.versicherung.setText(templateDTO.getConsumer());
                VBox.setMargin(node, new Insets(5, 0, 5, 0));
                contentPane.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
