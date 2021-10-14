package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.api.dto.FieldDTO;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.api.fields.FieldType;
import de.baleipzig.pdfextraction.client.connector.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.utils.PDFPreview;
import de.baleipzig.pdfextraction.client.view.ImportView;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import java.util.stream.Stream;

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

    @Inject
    private TemplateConnector connector;

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

        Set<FieldTypeWrapper> fieldTypes = FieldType.getAllFieldTypes()
                .stream()
                .filter(Predicate.not(fieldNames::contains))
                .map(FieldTypeWrapper::new)
                .collect(Collectors.toSet());

        if (fieldTypes.isEmpty()) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Warnung", "", "Es sind bereits alle verfügbaren Typen hinzugefügt worden.");
            return;
        }


        ChoiceDialog<FieldTypeWrapper> dialog = new ChoiceDialog<>(fieldTypes.iterator().next(), fieldTypes);
        dialog.setTitle("Feld-Typ");
        dialog.setHeaderText("Wähle einen Feld-Typ");


        dialog.showAndWait().ifPresent(fieldType -> {
            AtomicReference<Rectangle> start = new AtomicReference<>(null);
            final Paint color = getColor();

            final Scene scene = this.pdfPreview.getScene();
            final Stage stage = (Stage) scene.getWindow();
            final String oldTitle = stage.getTitle();

            stage.setTitle("%s| Auswählen: %s".formatted(oldTitle != null ? oldTitle : "", fieldType));

            pdfPreview.setOnMouseEntered(event -> scene.setCursor(Cursor.CROSSHAIR));
            pdfPreview.setOnMouseExited(event -> scene.setCursor(Cursor.DEFAULT));

            pdfPreview.setOnMousePressed(event -> {
                //Press == Start-Pos
                final double startX = event.getSceneX();
                final double startY = event.getSceneY();
                final Rectangle rec = new Rectangle(startX, startY, 0, 0);
                rec.setStroke(color);
                rec.setFill(Color.TRANSPARENT);
                pdfPreview.getChildren().add(rec);
                start.set(rec);
                event.consume();

                //Ermöglich das Zeichnen während des Ziehens
                pdfPreview.setOnMouseDragged(ev -> {
                    final ImageView imageView = pdfGridController.pdfPreviewImageView;

                    //Prüft das nicht über den Rand des Bildes gegangen wird.
                    if (ev.getX() > imageView.getX() && ev.getX() < imageView.getX() + imageView.getFitWidth()) {
                        final boolean isBackwardsDragged = startX > ev.getX();
                        if (isBackwardsDragged) {
                            rec.setX(ev.getX());
                        }

                        rec.setWidth(Math.abs(ev.getX() - startX));
                    }

                    if (ev.getY() > imageView.getY() && ev.getY() < imageView.getY() + imageView.getFitHeight()) {
                        final boolean isBackwardsDragged = startY > ev.getY();
                        if (isBackwardsDragged) {
                            rec.setY(ev.getY());
                        }

                        rec.setHeight(Math.abs(ev.getY() - startY));
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
                final Box box = new Box(PDFPreview.getInstance().getCurrentPage(), fieldType.type, rec, color);

                //Panel in der Box rechts
                final int count = datagrid.getRowCount();
                final Rectangle dot = new Rectangle(10, 10, color);
                final Label label = new Label(fieldType.toString());
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
                scene.setCursor(Cursor.DEFAULT);
                pdfPreview.setOnMouseEntered(ev -> scene.setCursor(Cursor.DEFAULT));
                stage.setTitle(oldTitle);
            });
        });
    }

    private Set<FieldType> getCurrentFieldNames() {
        return this.chosenFieldtypes.stream()
                .map(Box::type)
                .collect(Collectors.toSet());
    }

    private Paint getColor() {
        final Set<Paint> usedColors = this.chosenFieldtypes.stream()
                .map(Box::color)
                .collect(Collectors.toSet());

        final List<Color> freeToUse = Stream.of(Color.CRIMSON, Color.DARKGREEN, Color.MEDIUMBLUE, Color.DEEPPINK, Color.GREEN, Color.INDIGO, Color.RED)
                .filter(Predicate.not(usedColors::contains))
                .toList();

        if (freeToUse.isEmpty()) {
            return Color.BLACK;//Default
        }

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        return freeToUse.get(random.nextInt(0, freeToUse.size() - 1));
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

        final List<FieldDTO> fields = new ArrayList<>(this.chosenFieldtypes.size());

        final ImageView image = this.pdfGridController.pdfPreviewImageView;
        for (final Box box : this.chosenFieldtypes) {
            final Rectangle rec = box.place;

            //Hier sollten nicht die absolute Weite genommen werden, da wir das Bild ja skaliert haben
            final double percX = (rec.getX() - image.getX()) / image.getFitWidth();
            final double percY = (rec.getY() - image.getY()) / image.getFitHeight();
            final double percWidth = rec.getWidth() / image.getFitWidth();
            final double percHeight = rec.getHeight() / image.getFitHeight();

            fields.add(new FieldDTO(box.type, box.page, percX, percY, percWidth, percHeight));
        }

        final TemplateDTO toSave = new TemplateDTO(this.templateNameTextField.getText(), this.insuranceTextField.getText(), fields);//unused

        this.connector.save(toSave)
                .doOnSuccess(v -> Platform.runLater(() -> AlertUtils.showAlert(Alert.AlertType.INFORMATION,
                        "Erfolgreich",
                        null,
                        "Template wurde erstellt"
                )))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showAlert(Alert.AlertType.ERROR,
                        "Fehler",
                        "Ein Fehler ist aufgetreten.",
                        err.getLocalizedMessage()
                )))
                .subscribe();


        ControllerUtils.switchScene(
                (Stage) this.dataGridPane.getScene().getWindow(),
                new ImportView()
        );
    }

    public void cancelButtonOnAction() {

        ControllerUtils.switchScene(
                (Stage) this.dataGridPane.getScene().getWindow(),
                new ImportView()
        );
    }

    private boolean isDataIncomplete() {
        return !getCurrentFieldNames().containsAll(FieldType.getAllFieldTypes())
                || insuranceTextField.getText().isBlank()
                || templateNameTextField.getText().isBlank();
    }

    private record Box(int page, FieldType type, Rectangle place, Paint color) {
    }

    private record FieldTypeWrapper(FieldType type) {
        @Override
        public String toString() {
            return this.type.getName();
        }
    }
}
