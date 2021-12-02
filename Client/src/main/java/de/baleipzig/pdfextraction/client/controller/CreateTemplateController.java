package de.baleipzig.pdfextraction.client.controller;

import com.jfoenix.controls.JFXButton;
import de.baleipzig.pdfextraction.api.dto.FieldDTO;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.api.fields.FieldType;
import de.baleipzig.pdfextraction.client.connector.api.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.Box;
import de.baleipzig.pdfextraction.client.utils.EventUtils;
import de.baleipzig.pdfextraction.client.utils.PDFRenderer;
import de.baleipzig.pdfextraction.client.view.Imports;
import de.baleipzig.pdfextraction.client.utils.ColorPicker;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CreateTemplateController extends Controller implements Initializable{

    private final Set<Box> chosenFieldTypes = new HashSet<>();

    @FXML
    public MenuBar menuBar;

    @FXML
    public AnchorPane pdfAnchor;

    @FXML
    private GridPane dataGridPane;

    @FXML
    private TextField insuranceTextField;

    @FXML
    private TextField templateNameTextField;

    @FXML
    private PdfPreviewController pdfPreviewController;

    @FXML
    private MenuBarController menuBarController;

    @FXML
    private GridPane datagrid;

    @Inject
    protected TemplateConnector connector;

    @Inject
    protected PDFRenderer renderer;

    private static void doNothing(MouseEvent ev) {
        //this should do nothing, used in the Handler to reset them
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changeFocusOnControlParent(menuBar);
        EventUtils.chainAfterOnAction(this.pdfPreviewController.pageBackButton, this::onPageTurn);
        EventUtils.chainAfterOnAction(this.pdfPreviewController.pageForwardButton, this::onPageTurn);
        EventUtils.chainAfterOnAction(this.menuBarController.chooseFile, this.pdfPreviewController::updatePdfPreview);
    }

    private void onPageTurn() {
        final int currentPage = this.renderer.getCurrentPage();

        final List<Rectangle> toAdd = chosenFieldTypes.stream()
                .filter(b -> b.page() == currentPage)
                .map(Box::place)
                .toList();

        final List<Rectangle> all = chosenFieldTypes.stream()
                .map(Box::place)
                .toList();

        final List<Node> children = this.pdfAnchor.getChildren();
        children.removeAll(all);
        children.addAll(toAdd);
    }

    @FXML
    private void addFieldButtonOnClick() {

        if (!this.renderer.hasPreview()) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, getResource("warningTitle"), null, getResource("alertChoosePDF"));
            return;
        }

        final List<FieldTypeWrapper> fieldTypes = getAvailableFieldTypes();

        if (fieldTypes.isEmpty()) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, getResource("warningTitle"), "", getResource("alertAllTypesUsed"));
            return;
        }


        final ChoiceDialog<FieldTypeWrapper> dialog = new ChoiceDialog<>(fieldTypes.iterator().next(), fieldTypes);
        dialog.setTitle(getResource("titleAddFieldDialog"));
        dialog.setHeaderText(getResource("headerAddFieldDialog"));


        dialog.showAndWait().ifPresent(fieldType -> {
            final Rectangle rec = new Rectangle(0, 0, 0, 0);
            final Paint color = new ColorPicker(this.chosenFieldTypes).getColor();
            rec.setStroke(color);
            rec.setFill(Color.TRANSPARENT);
            final Scene scene = this.pdfAnchor.getScene();
            final Stage stage = (Stage) scene.getWindow();
            final String oldTitle = stage.getTitle();

            stage.setTitle("%s | %s: %s".formatted(oldTitle != null ? oldTitle : "", getResource("choose"), fieldType));

            this.pdfAnchor.setOnMouseEntered(event -> scene.setCursor(Cursor.CROSSHAIR));
            this.pdfAnchor.setOnMouseExited(event -> scene.setCursor(Cursor.DEFAULT));

            this.pdfAnchor.setOnMousePressed(event -> startDrawingRectangle(rec, event));

            this.pdfAnchor.setOnMouseReleased(ev -> finishDrawingRectangle(fieldType, rec, color, scene, stage, oldTitle, ev));
        });
    }

    private void startDrawingRectangle(final Rectangle rec, final MouseEvent event) {
        //Press == Start-Pos
        final double startX = event.getX();
        final double startY = event.getY();
        this.pdfAnchor.getChildren().add(rec);
        rec.setX(startX);
        rec.setY(startY);
        event.consume();

        //Ermöglich das Zeichnen während des Ziehens
        this.pdfAnchor.setOnMouseDragged(ev -> paintRectangleContinuously(startX, startY, rec, ev));

        //Der Handler hier soll nicht nochmal aufgerufen werden
        this.pdfAnchor.setOnMousePressed(CreateTemplateController::doNothing);
        ((Stage) this.pdfAnchor.getScene().getWindow()).setResizable(false);
    }

    private void finishDrawingRectangle(FieldTypeWrapper fieldType, Rectangle rec, Paint color, Scene scene, Stage stage, String oldTitle, MouseEvent ev) {
        //Released == End-Pos

        //Box im Preview
        final Box box = new Box(this.renderer.getCurrentPage(), fieldType.type, rec, color);

        //Panel in der Box rechts
        final int count = this.datagrid.getRowCount();
        final Rectangle dot = new Rectangle(20, 20, color);
        final Label label = new Label(fieldType.toString());
        final JFXButton remove = new JFXButton("Remove");
        remove.getStyleClass().add("button-white");
        this.datagrid.addRow(count, dot, label, remove);
        remove.setOnAction(e -> onRemoveButton(rec, box, dot, label, remove));
        this.chosenFieldTypes.add(box);

        ev.consume();
        //Die Handler sind fertig, also ersetzen mit welchen, die nichts tun
        this.pdfAnchor.setOnMouseReleased(CreateTemplateController::doNothing);
        this.pdfAnchor.setOnMouseDragged(CreateTemplateController::doNothing);
        this.pdfAnchor.setOnMouseEntered(CreateTemplateController::doNothing);
        scene.setCursor(Cursor.DEFAULT);
        stage.setTitle(oldTitle);
    }

    private void onRemoveButton(Rectangle rec, Box box, Rectangle dot, Label label, JFXButton remove) {
        this.datagrid.getChildren().removeAll(dot, label, remove);
        this.chosenFieldTypes.remove(box);
        this.pdfAnchor.getChildren().remove(rec);
    }


    private void paintRectangleContinuously(double startX, double startY, Rectangle rec, MouseEvent ev) {
        final ImageView imageView = this.pdfPreviewController.pdfPreviewImageView;

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
        return this.chosenFieldTypes.stream()
                .map(Box::type)
                .collect(Collectors.toSet());
    }

    @FXML
    private void createTemplateButtonOnAction() {
        if (isDataIncomplete()) {
            AlertUtils.showErrorAlert(getResource("alertFillAllFields"));
            return;
        }

        final List<FieldDTO> fields = new ArrayList<>(this.chosenFieldTypes.size());

        final ImageView image = this.pdfPreviewController.pdfPreviewImageView;
        final Bounds bounds = image.getBoundsInParent();
        for (final Box box : this.chosenFieldTypes) {
            final Rectangle rec = box.place();

            //Hier sollten nicht die absolute Weite genommen werden, da wir das Bild ja skaliert haben
            final double percX = (rec.getX() - AnchorPane.getLeftAnchor(image)) / bounds.getWidth();
            final double percY = (rec.getY() - AnchorPane.getTopAnchor(image)) / bounds.getHeight();
            final double percWidth = rec.getWidth() / bounds.getWidth();
            final double percHeight = rec.getHeight() / bounds.getHeight();

            fields.add(new FieldDTO(box.type(), box.page(), percX, percY, percWidth, percHeight));
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
                getResource("successTitle"),
                null,
                getResource("alertTemplateCreated")
        );

        ((Stage) this.pdfAnchor.getScene().getWindow()).setResizable(true);

        switchScene((Stage) this.dataGridPane.getScene().getWindow(), new Imports());
    }

    private void onFailedSave(Throwable err) {
        AlertUtils.showErrorAlert(err);
    }

    @FXML
    private void cancelButtonOnAction() {
        ((Stage) this.pdfAnchor.getScene().getWindow()).setResizable(true);
        switchScene((Stage) this.dataGridPane.getScene().getWindow(), new Imports());
    }

    private boolean isDataIncomplete() {
        return !getCurrentFieldNames().containsAll(FieldType.getAllFieldTypes())
                || insuranceTextField.getText().isBlank()
                || templateNameTextField.getText().isBlank();
    }

    private record FieldTypeWrapper(FieldType type) {

        @Override
        public String toString() {
            return this.type.getName();
        }
    }
}
