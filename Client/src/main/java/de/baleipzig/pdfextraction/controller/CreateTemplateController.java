package de.baleipzig.pdfextraction.controller;

import de.baleipzig.pdfextraction.api.dto.Field;
import de.baleipzig.pdfextraction.api.dto.Template;
import de.baleipzig.pdfextraction.client.PDFPreview;
import de.baleipzig.pdfextraction.common.alert.AlertUtils;
import de.baleipzig.pdfextraction.common.controller.ControllerUtils;
import de.baleipzig.pdfextraction.fieldtype.FieldType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CreateTemplateController implements Initializable {

    private final Set<Box> chosenFieldtypes = new HashSet<>();
    @FXML
    public GridPane dataGridPane;
    @FXML
    public TextField insuranceTextField;
    @FXML
    public TextField templateNameTextField;
    @FXML
    public AnchorPane pdfPreview;
    @FXML
    public PdfPreviewIncludeController pdfGridController;
    @FXML
    public GridPane datagrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> ((Stage) this.pdfPreview.getScene().getWindow()).setResizable(false));

        final EventHandler<ActionEvent> superForward = pdfGridController.pageForwardButton.getOnAction();
        pdfGridController.pageForwardButton.setOnAction(ev -> {
            superForward.handle(ev);
            this.onPageTurn();
        });

        final EventHandler<ActionEvent> superBackward = pdfGridController.pageBackButton.getOnAction();
        pdfGridController.pageBackButton.setOnAction(ev -> {
            superBackward.handle(ev);
            this.onPageTurn();
        });
    }

    private void onPageTurn() {
        final int currentPage = PDFPreview.getInstance().getCurrentPage();

        final List<Rectangle> toAdd = chosenFieldtypes.stream()
                .filter(b -> b.page == currentPage)
                .map(Box::place)
                .toList();

        final List<Rectangle> all = chosenFieldtypes.stream()
                .map(Box::place)
                .toList();

        final List<Node> children = this.pdfPreview.getChildren();
        children.removeAll(all);
        children.addAll(toAdd);
    }

    @FXML
    public void addFieldButtonOnClick() {

        if (!PDFPreview.getInstance().hasPreview()) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Warnung", null, "Bitte wählen sie zuerst ein PDF aus.");
            return;
        }

        final Set<FieldType> fieldNames = getCurrentFieldNames();

        Set<FieldType> fieldTypes = FieldType.getAllFieldTypes()
                .stream()
                .filter(Predicate.not(fieldNames::contains))
                .collect(Collectors.toSet());

        if (fieldTypes.isEmpty()) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Warnung", "", "Es sind bereits alle verfügbaren Typen hinzugefügt worden.");
            return;
        }


        ChoiceDialog<FieldType> dialog = new ChoiceDialog<>(fieldTypes.iterator().next(), fieldTypes);
        dialog.setTitle("Feld-Typ");
        dialog.setHeaderText("Wähle einen Feld-Typ");


        dialog.showAndWait().ifPresent(fieldType -> {
            AtomicReference<Rectangle> start = new AtomicReference<>(null);
            final Paint color = getColor();

            pdfPreview.setOnMousePressed(event -> {
                //Press == Start-Pos
                final double x = event.getSceneX();
                final double y = event.getSceneY();

                final Rectangle rec = new Rectangle(x, y, 0, 0);
                rec.setStroke(color);
                rec.setFill(Color.TRANSPARENT);
                pdfPreview.getChildren().add(rec);
                start.set(rec);
                event.consume();

                //Ermöglich das Zeichnen während des Ziehens
                pdfPreview.setOnMouseDragged(ev -> {
                    final ImageView imageView = pdfGridController.pdfPreviewImageView;

                    //Prüft das nicht über den Rand des Bildes gegangen wird.
                    if (ev.getX() < imageView.getX() + imageView.getFitWidth()) {
                        rec.setWidth(ev.getX() - x);
                    }

                    if (ev.getY() < imageView.getY() + imageView.getFitHeight()) {
                        rec.setHeight(ev.getY() - y);
                    }

                    ev.consume();
                });

                //Der Handler hier soll nicht nochmal aufgerufen werden
                pdfPreview.setOnMousePressed(ev -> {
                });
            });


            pdfPreview.setOnMouseReleased(event -> {
                //Released == End-Pos
                final Rectangle rec = start.get();


                //Box im Preview
                final Box box = new Box(PDFPreview.getInstance().getCurrentPage(), fieldType, rec);

                //Panel in der Box rechts
                final int count = datagrid.getRowCount();
                final Rectangle dot = new Rectangle(10, 10, color);
                final Label label = new Label(fieldType.getName());
                final Button remove = new Button("Remove");
                datagrid.addRow(count, dot, label, remove);
                remove.setOnAction(e -> {
                    datagrid.getChildren().removeAll(dot, label, remove);
                    chosenFieldtypes.remove(box);
                    pdfPreview.getChildren().remove(rec);
                });

                chosenFieldtypes.add(box);
                event.consume();

                //Die Handler sind fertig, also ersetzen mit welchen, die nichts tun
                pdfPreview.setOnMouseReleased(ev -> {
                });
                pdfPreview.setOnMouseDragged(ev -> {
                });
            });
        });
    }

    private Set<FieldType> getCurrentFieldNames() {
        return this.chosenFieldtypes.stream()
                .map(Box::type)
                .collect(Collectors.toSet());
    }

    private Paint getColor() {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        return Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    @SuppressWarnings({"java:S1854", "java:S1481", "java:S1135"}) //Remove when DB is merged (PAN-19)
    public void createTemplateButtonOnAction() {

        if (isDataIncomplete()) {
            AlertUtils.showAlert(Alert.AlertType.ERROR,
                    "Fehler",
                    "Es müssen alle Felder ausgefüllt sein",
                    ""
            );
            return;
        }

        final List<Field> fields = new ArrayList<>(this.chosenFieldtypes.size());

        final ImageView image = this.pdfGridController.pdfPreviewImageView;
        for (final Box box : this.chosenFieldtypes) {
            final Rectangle rec = box.place;

            //Hier sollten nicht die absolute Weite genommen werden, da wir das Bild ja skaliert haben
            final double percX = (rec.getX() - image.getX()) / image.getFitWidth();
            final double percY = (rec.getY() - image.getY()) / image.getFitHeight();
            final double percWidth = rec.getWidth() / image.getFitWidth();
            final double percHeight = rec.getHeight() / image.getFitHeight();

            fields.add(new Field(box.type, box.page, percX, percY, percWidth, percHeight));
        }

        final Template toSave = new Template(this.templateNameTextField.getText(), this.insuranceTextField.getText(), fields);

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

    public void cancelButtonOnAction() {

        ControllerUtils.switchScene(
                (Stage) this.dataGridPane.getScene().getWindow(),
                getClass().getResource("/view/ImportView.fxml")
        );
    }

    private boolean isDataIncomplete() {
        final List<FieldType> allTypes = FieldType.getAllFieldTypes()
                .stream()
                .toList();

        return !getCurrentFieldNames().containsAll(allTypes)
                || insuranceTextField.getText().isBlank()
                || templateNameTextField.getText().isBlank();
    }

    private record Box(int page, FieldType type, Rectangle place) {
    }
}
