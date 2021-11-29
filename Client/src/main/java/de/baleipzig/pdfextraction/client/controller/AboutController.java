package de.baleipzig.pdfextraction.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXButton;
import de.baleipzig.pdfextraction.client.utils.Dependencie;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AboutController extends Controller implements Initializable {

    @FXML
    public GridPane dependenciesGrid;

    @FXML
    public JFXButton closeButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try (InputStream inputStream = MenuBarController.class.getResourceAsStream("../view/dependencies.json")){

            ObjectMapper mapper = new ObjectMapper();
            List<Dependencie> dependencies = List.of(mapper.readValue(inputStream, Dependencie[].class));

            createTableHeaders();
            fillTableWithContent(dependencies);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createTableHeaders() {
        // Table Headers
        Label name = new Label(getResource("Software"));
        name.getStyleClass().add("aboutViewTableHeader");
        Label license = new Label(getResource("License"));
        license.getStyleClass().add("aboutViewTableHeader");
        this.dependenciesGrid.addRow(dependenciesGrid.getRowCount(), name, license);
    }

    private void fillTableWithContent(List<Dependencie> dependencies) {
        dependencies.forEach(dep -> {

            int row = dependenciesGrid.getRowCount();
            Label name = new Label(dep.getName() + " " + dep.getVersion());
            Hyperlink link= new Hyperlink(dep.getLicense());

            link.setOnAction(evt -> {
                try {
                    Desktop.getDesktop().browse(new URI(dep.getLink()));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            });

            this.dependenciesGrid.addRow(row, name, link);
        });
    }

    public void cancelButtonOnAction() {

        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
