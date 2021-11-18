package de.baleipzig.pdfextraction.client.controller;

import com.jfoenix.controls.JFXButton;
import de.baleipzig.pdfextraction.client.connector.impl.TemplateConnectorImpl;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class TemplateItemController extends Controller implements Initializable {

    @FXML
    public Label templatename;

    @FXML
    public Label versicherung;

    @FXML
    public JFXButton deleteButton;

    @FXML
    public JFXButton editButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
