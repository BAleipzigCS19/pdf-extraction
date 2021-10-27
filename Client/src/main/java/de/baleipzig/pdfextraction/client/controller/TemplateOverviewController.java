package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.client.connector.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.view.Imports;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TemplateOverviewController implements Initializable {

    @FXML
    public VBox contentPane;

    @Inject
    private TemplateConnector connector;

    private List<TemplateDTO> templateDTOList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        templateDTOList.add(new TemplateDTO("Kfz-Versicherung1", "Test-versicherung1", null));
        templateDTOList.add(new TemplateDTO("Kfz-Versicherung2", "Test-versicherung2", null));
        templateDTOList.add(new TemplateDTO("Kfz-Versicherung3", "Test-versicherung3", null));

        Platform.runLater(() -> {
            contentPane.setSpacing(10);
            for (TemplateDTO templateDto: templateDTOList) {
                HBox hBox = new HBox();

                Label name = new Label(templateDto.getName());
                name.setFont(new Font(15));
                Label consumer = new Label(templateDto.getConsumer());
                VBox vBox = new VBox(name, consumer);

                Button editTemplateButton = new Button("Bearbeiten");
                editTemplateButton.setOnAction(event -> {
                    //Template bearbeiten
                });

                Button deleteTemplateButton = new Button("Löschen");
                deleteTemplateButton.setOnAction(event -> {
                    //Template löschen
                });
                VBox vBoxButtons = new VBox(editTemplateButton, deleteTemplateButton);

                hBox.getChildren().add(0, vBox);
                hBox.getChildren().add(1, vBoxButtons);
                hBox.setBorder(new Border(new BorderStroke(
                        Color.BLACK,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        BorderWidths.DEFAULT)));
                hBox.setSpacing(10);
                contentPane.getChildren().add(hBox);
            }

            Button backButton = new Button("Zurück");
            backButton.setOnAction(event -> {
                ControllerUtils.switchScene((Stage) this.contentPane.getScene().getWindow(), new Imports());
            });
            contentPane.getChildren().add(backButton);
        });

        this.connector.getAllNames()
                .doOnError(err -> LoggerFactory.getLogger(ImportController.class)
                        .error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(this::onRequestCompleted);
    }

    private void onRequestCompleted(final String name) {

        /*
        Platform.runLater(() -> {
            VBox vBox = new VBox(new Label(name), new Label());
            vBox.setBorder(new Border(new BorderStroke(
                    Color.BLACK,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    BorderWidths.DEFAULT)));
            contentPane.getChildren().add(vBox);
            contentPane.setSpacing(10);
        });

         */
    }
}
