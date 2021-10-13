package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.connector.TemplateConnector;
import de.baleipzig.pdfextraction.client.fieldtype.FieldTypes;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.view.ImportView;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CreateTemplateController implements Initializable {

    @FXML
    public GridPane dataGridPane;

    @FXML
    public AnchorPane fieldAnchorPane;

    @FXML
    public TextField insuranceTextField;

    @FXML
    public TextField templateNameTextField;

    @FXML
    public Button cancelButton;

    @FXML
    public Button createTemplateButton;

    @FXML
    public Button addFieldButton;

    @Inject
    private TemplateConnector connector;

    private final VBox vBox = new VBox(10);

    private final Set<String> chosenFieldtypes = new HashSet<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fieldAnchorPane.getChildren().add(vBox);
    }

    @FXML
    public void addFieldButtonOnClick() {

        Set<String> fieldTypes = FieldTypes.getAllFieldTypes()
                .stream()
                .map(FieldTypes::getName)
                .filter(Predicate.not(this.chosenFieldtypes::contains))
                .collect(Collectors.toSet());

        if (fieldTypes.isEmpty()) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Warnung", "", "Es sind bereits alle verfügbaren Typen hinzugefügt worden.");
            return;
        }


        ChoiceDialog<String> dialog = new ChoiceDialog<>(fieldTypes.iterator().next(), fieldTypes);
        dialog.setTitle("Feld-Typ");
        dialog.setHeaderText("Wähle einen Feld-Typ");

        dialog.showAndWait().ifPresent(fieldType -> {
            chosenFieldtypes.add(fieldType);
            Pane pane = new Pane();
            pane.setPrefWidth(200);
            pane.setPrefHeight(30);
            pane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            vBox.getChildren().addAll(new Label(fieldType), pane);
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
                    new ImportView()
            );
        }
    }

    public void cancelButtonOnAction() {

        ControllerUtils.switchScene(
                (Stage) this.dataGridPane.getScene().getWindow(),
                new ImportView()
        );
    }

    private boolean isDataIncomplete() {
        final List<String> allTypes = FieldTypes.getAllFieldTypes()
                .stream()
                .map(FieldTypes::getName)
                .toList();

        return !this.chosenFieldtypes.containsAll(allTypes)
                || insuranceTextField.getText().isBlank()
                || templateNameTextField.getText().isBlank()
                || fieldAnchorPane.getChildren().isEmpty();
    }
}
