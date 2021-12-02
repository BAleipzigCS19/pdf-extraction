package de.baleipzig.pdfextraction.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXButton;
import de.baleipzig.pdfextraction.client.utils.Dependency;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;


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

    @FXML
    public Hyperlink gitHubLink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try (InputStream inputStream = MenuBarController.class.getResourceAsStream("../view/dependencies.json")){

            ObjectMapper mapper = new ObjectMapper();
            List<Dependency> dependencies = List.of(mapper.readValue(inputStream, Dependency[].class));

            gitHubLink.setOnAction(event -> onURLclick("https://github.com/BAleipzigCS19/pdf-extraction"));
            createTableHeaders();
            fillTableWithContent(dependencies);

        } catch (Exception e) {
            LoggerFactory.getLogger(AboutController.class)
                    .atError()
                    .setCause(e)
                    .log("Exception while loading ressource \"dependencies.json\" ");
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

    private void fillTableWithContent(List<Dependency> dependencies) {
        for (Dependency dependency : dependencies) {

            int row = dependenciesGrid.getRowCount();
            Hyperlink name = new Hyperlink(dependency.getName() + " " + dependency.getVersion());
            Label license= new Label(dependency.getLicense());
            name.setOnAction(event -> onURLclick(dependency.getLink()));

            this.dependenciesGrid.addRow(row, name, license);
        }
    }

    private void onURLclick(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            LoggerFactory.getLogger(AboutController.class)
                    .atError()
                    .setCause(e)
                    .addArgument(url)
                    .log("Exception while resolving link: {} ");
        }
    }


    public void cancelButtonOnAction() {

        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
