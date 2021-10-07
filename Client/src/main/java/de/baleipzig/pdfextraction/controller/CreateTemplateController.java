package de.baleipzig.pdfextraction.controller;

import de.baleipzig.pdfextraction.common.alert.AlertUtils;
import de.baleipzig.pdfextraction.common.controller.ControllerUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CreateTemplateController implements Initializable {

    @FXML
    public GridPane dataGridPane;
    private final VBox vBox = new VBox(10);
    public AnchorPane fieldAnchorPane;
    public TextField insuranceTextField;
    public TextField templateNameTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fieldAnchorPane.getChildren().add(vBox);
    }

    @FXML
    public void AddFieldButtonOnClick() {

        //TODO Liste aller unterstützten FieldTypes noch hinzufügen
        List<String> choices = new ArrayList<>();
        choices.add("Field-Typ 1");
        choices.add("Field-Typ 2");
        choices.add("Field-Typ 3");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Field-Typ 1", choices);
        dialog.setTitle("Feld-Typ");
        dialog.setHeaderText("Wähle einen Feld-Typ");

        dialog.showAndWait().ifPresent(fieldType -> {
            vBox.getChildren().add(new Label(fieldType));
            Pane pane = new Pane();
            pane.setPrefWidth(200);
            pane.setPrefHeight(30);
            pane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            vBox.getChildren().add(pane);
        });
    }

    public void createTemplateButtonOnAction() {

        if (isDataIncomplete()) {
            AlertUtils.showAlert(Alert.AlertType.ERROR,
                    "Fehler",
                    "Es müssen alle Felder ausgefüllt sein",
                    ""
            );
        } else {
            //TODO hier müssen die Daten in der DB gespeichert werden
            AlertUtils.showAlert(Alert.AlertType.INFORMATION,
                    "Erfolgreich",
                    "Template wurde erstellt",
                    ""
            );

            ControllerUtils.switchScene(
                    (Stage) this.dataGridPane.getScene().getWindow(),
                    getClass().getResource("/view/ImportView.fxml")
            );
        }
    }

    private boolean isDataIncomplete() {

        return insuranceTextField.getText().trim().isEmpty() || templateNameTextField.getText().trim().isEmpty()
                || fieldAnchorPane.getChildren().size() == 0;
    }
}
