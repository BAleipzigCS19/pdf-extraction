package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.api.dto.FieldDTO;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.api.fields.FieldType;
import de.baleipzig.pdfextraction.client.connector.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.utils.PDFRenderer;
import de.baleipzig.pdfextraction.client.utils.interfaces.Controller;
import de.baleipzig.pdfextraction.client.view.Imports;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateTemplateController implements Initializable, Controller {

    private final Set<Box> chosenFieldtypes = new HashSet<>();

    @FXML
    private GridPane dataGridPane;

    @FXML
    private TextField insuranceTextField;

    @FXML
    private TextField templateNameTextField;

    @FXML
    private AnchorPane pdfPreview;

    @FXML
    private PdfPreviewController pdfGridController;

    @FXML
    private GridPane datagrid;

    @Inject
    private TemplateConnector connector;

    private static void doNothing(MouseEvent ev) {
        //this should do nothing, used in the Handler to reset them
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        final EventHandler<ActionEvent> superForward = pdfGridController.pageForwardButton.getOnAction();
        this.pdfGridController.pageForwardButton.setOnAction(ev -> {
            superForward.handle(ev);
            this.onPageTurn();
        });

        final EventHandler<ActionEvent> superBackward = pdfGridController.pageBackButton.getOnAction();
        this.pdfGridController.pageBackButton.setOnAction(ev -> {
            superBackward.handle(ev);
            this.onPageTurn();
        });
    }

    private void onPageTurn() {
        final int currentPage = PDFRenderer.getInstance().getCurrentPage();

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
    private void addFieldButtonOnClick() {

        if (!PDFRenderer.getInstance().hasPreview()) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Warnung", null, "Bitte wählen sie zuerst ein PDF aus.");
            return;
        }

        final List<FieldTypeWrapper> fieldTypes = getAvailableFieldTypes();

        if (fieldTypes.isEmpty()) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Warnung", "", "Es sind bereits alle verfügbaren Typen hinzugefügt worden.");
            return;
        }


        final ChoiceDialog<FieldTypeWrapper> dialog = new ChoiceDialog<>(fieldTypes.iterator().next(), fieldTypes);
        dialog.setTitle("Feld-Typ");
        dialog.setHeaderText("Wähle einen Feld-Typ");


        dialog.showAndWait().ifPresent(fieldType -> {
            final Rectangle rec = new Rectangle(0, 0, 0, 0);
            final Paint color = getColor();
            rec.setStroke(color);
            rec.setFill(Color.TRANSPARENT);
            final Scene scene = this.pdfPreview.getScene();
            final Stage stage = (Stage) scene.getWindow();
            final String oldTitle = stage.getTitle();

            stage.setTitle("%s| Auswählen: %s".formatted(oldTitle != null ? oldTitle : "", fieldType));

            this.pdfPreview.setOnMouseEntered(event -> scene.setCursor(Cursor.CROSSHAIR));
            this.pdfPreview.setOnMouseExited(event -> scene.setCursor(Cursor.DEFAULT));

            this.pdfPreview.setOnMousePressed(event -> startDrawingRectangle(rec, event));

            this.pdfPreview.setOnMouseReleased(ev -> finishDrawingRectangle(fieldType, rec, color, scene, stage, oldTitle, ev));
        });
    }

    private void startDrawingRectangle(final Rectangle rec, final MouseEvent event) {
        //Press == Start-Pos
        final double startX = event.getSceneX();
        final double startY = event.getSceneY();
        this.pdfPreview.getChildren().add(rec);
        rec.setX(startX);
        rec.setY(startY);
        event.consume();

        //Ermöglich das Zeichnen während des Ziehens
        pdfPreview.setOnMouseDragged(ev -> paintRectangleContinuously(startX, startY, rec, ev));

        //Der Handler hier soll nicht nochmal aufgerufen werden
        pdfPreview.setOnMousePressed(CreateTemplateController::doNothing);
        ((Stage) this.pdfPreview.getScene().getWindow()).setResizable(false);
    }

    private void finishDrawingRectangle(FieldTypeWrapper fieldType, Rectangle rec, Paint color, Scene scene, Stage stage, String oldTitle, MouseEvent ev) {
        //Released == End-Pos

        //Box im Preview
        final Box box = new Box(PDFRenderer.getInstance().getCurrentPage(), fieldType.type, rec, color);

        //Panel in der Box rechts
        final int count = this.datagrid.getRowCount();
        final Rectangle dot = new Rectangle(10, 10, color);
        final Label label = new Label(fieldType.toString());
        final Button remove = new Button("Remove");
        this.datagrid.addRow(count, dot, label, remove);
        remove.setOnAction(e -> {
            this.datagrid.getChildren().removeAll(dot, label, remove);
            this.chosenFieldtypes.remove(box);
            this.pdfPreview.getChildren().remove(rec);
        });

        this.chosenFieldtypes.add(box);

        ev.consume();
        //Die Handler sind fertig, also ersetzen mit welchen, die nichts tun
        this.pdfPreview.setOnMouseReleased(CreateTemplateController::doNothing);
        this.pdfPreview.setOnMouseDragged(CreateTemplateController::doNothing);
        this.pdfPreview.setOnMouseEntered(CreateTemplateController::doNothing);
        scene.setCursor(Cursor.DEFAULT);
        stage.setTitle(oldTitle);
    }

    private void paintRectangleContinuously(double startX, double startY, Rectangle rec, MouseEvent ev) {
        final ImageView imageView = this.pdfGridController.pdfPreviewImageView;

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
    }

    private List<FieldTypeWrapper> getAvailableFieldTypes() {
        final Set<FieldType> fieldNames = getCurrentFieldNames();

        return FieldType.getAllFieldTypes()
                .stream()
                .filter(Predicate.not(fieldNames::contains))
                .sorted(Comparator.comparing(FieldType::getName))
                .map(FieldTypeWrapper::new)
                .toList();
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

    @FXML
    private void createTemplateButtonOnAction() {
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
        this.connector
                .save(toSave)
                .doOnSuccess(v -> Platform.runLater(this::onSuccessfulSave))
                .doOnError(err -> Platform.runLater(() -> this.onFailedSave(err)))
                .subscribe();
    }

    private void onSuccessfulSave() {
        AlertUtils.showAlert(Alert.AlertType.INFORMATION,
                "Erfolgreich",
                null,
                "Template wurde erstellt"
        );

        ((Stage) this.pdfPreview.getScene().getWindow()).setResizable(true);

        ControllerUtils.switchScene(
                (Stage) this.dataGridPane.getScene().getWindow(),
                new Imports()
        );
    }

    private void onFailedSave(Throwable err) {
        AlertUtils.showAlert(Alert.AlertType.ERROR,
                "Fehler",
                "Ein Fehler ist aufgetreten.",
                err.getLocalizedMessage());
    }

    @FXML
    private void cancelButtonOnAction() {
        ((Stage) this.pdfPreview.getScene().getWindow()).setResizable(true);
        ControllerUtils.switchScene(
                (Stage) this.dataGridPane.getScene().getWindow(),
                new Imports()
        );
    }

    private boolean isDataIncomplete() {
        return !getCurrentFieldNames().containsAll(FieldType.getAllFieldTypes())
                || insuranceTextField.getText().isBlank()
                || templateNameTextField.getText().isBlank();
    }

    private record Box(int page, FieldType type, Rectangle place, Paint color) {
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Box other)) {
                return false;
            }
            return page == other.page
                    && type == other.type
                    && Objects.equals(place, other.place)
                    && Objects.equals(color, other.color);
        }

        @Override
        public int hashCode() {
            return Objects.hash(page, type, place, color);
        }
    }

    private record FieldTypeWrapper(FieldType type) {

        @Override
        public String toString() {
            return this.type.getName();
        }
    }
}
