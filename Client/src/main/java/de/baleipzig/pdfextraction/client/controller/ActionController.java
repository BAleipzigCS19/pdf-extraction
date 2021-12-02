package de.baleipzig.pdfextraction.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import de.baleipzig.pdfextraction.client.connector.api.ExtractionConnector;
import de.baleipzig.pdfextraction.client.connector.api.ResultConnector;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.Job;
import de.baleipzig.pdfextraction.client.view.ActionItem;
import de.baleipzig.pdfextraction.client.view.Imports;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public class ActionController extends Controller implements Initializable {

    @FXML
    public MenuBar menuBar;

    @FXML
    public JFXButton runActionButton;

    @FXML
    public ProgressIndicator progress;

    @FXML
    public JFXMasonryPane contentPaneOthers;

    @FXML
    private Button backToImportButton;

    @FXML
    private JFXMasonryPane contentPane;

    @Inject
    private ExtractionConnector extractionConnector;

    @Inject
    private ResultConnector resultConnector;

    @Inject
    private Job job;

    private static final int STROKE_WIDTH_CIRCLE = 2;
    private final ImagePattern done = new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/de/baleipzig/pdfextraction/client/view/img/done.png"))));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changeFocusOnControlParent(menuBar);

        this.resultConnector.getAllNames()
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(name -> Platform.runLater(() -> onCompleteGetAllNames(name)));

        addCreateTestPictureItem();
    }

    @FXML
    private void backToImportButtonOnAction() {
        switchScene((Stage) this.backToImportButton.getScene().getWindow(), new Imports());
    }

    @FXML
    private void runActionButtonOnAction() {

        checkItemSelected(contentPane);
        checkItemSelected(contentPaneOthers);

    }

    private void toggleActionButtonVisible(boolean isVisible) {
        runActionButton.setVisible(isVisible);
        progress.setVisible(!isVisible);
    }

    private void onCompleteGetAllNames(String name) {

        FXMLLoader loader = new FXMLLoader(new ActionItem().getFXML());
        try {
            Node item = loader.load();
            buildItem(loader, name);
            contentPane.getChildren().add(item);
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass())
                    .atError()
                    .setCause(e)
                    .log("Exception occurred while create item");

            AlertUtils.showErrorAlert(e);
        }
    }

    private void buildItem(FXMLLoader loader, String name) {

        ActionItemController actionItemController = loader.getController();
        actionItemController.actionLabel.setText(name);
        actionItemController.itemPane.setOnMouseClicked(event -> {
            if (actionItemController.selectActionCircle.getStrokeWidth() == STROKE_WIDTH_CIRCLE) {
                GridPane gridPaneContent = checkOneActionIsSelected(Arrays.asList(contentPane, contentPaneOthers));
                if (gridPaneContent != null) {
                    getActionItemById(gridPaneContent, "hBoxCircle")
                            .map(box -> getActionItemById(box, "selectActionCircle"))
                            .map(Circle.class::cast)
                            .ifPresent(c -> {
                                c.setStrokeWidth(STROKE_WIDTH_CIRCLE);
                                c.setFill(Paint.valueOf("#ffffff00"));
                            });
                }


                actionItemController.selectActionCircle.setStrokeWidth(0);
                actionItemController.selectActionCircle.setFill(done);
            } else {
                actionItemController.selectActionCircle.setStrokeWidth(STROKE_WIDTH_CIRCLE);
                actionItemController.selectActionCircle.setFill(Paint.valueOf("#ffffff00"));
            }
        });

    }

    private GridPane checkOneActionIsSelected(List<JFXMasonryPane> contentPanes) {

        for (JFXMasonryPane panes : contentPanes) {
            for (Node node : panes.getChildren()) {

                Optional<Node> gridPaneContent = getActionItemById(node, "gridPaneContent");

                Optional<Circle> circle1 = gridPaneContent
                        .flatMap(content -> getActionItemById(content, "hBoxCircle"))
                        .flatMap(circle -> getActionItemById(circle, "selectActionCircle"))
                        .map(Circle.class::cast)
                        .filter(this::isItemSelected);

                if (circle1.isPresent()) {
                    return gridPaneContent.map(GridPane.class::cast).orElseThrow();
                }
            }
        }

        return null;
    }

    private void onSuccess(final byte[] pdfBytes) {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle(getResource("safeoutcomeFileChooser"));
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("pdf", "*.pdf"));
        final File toSave = chooser.showSaveDialog(this.menuBar.getScene().getWindow());
        if (toSave == null) {
            return;
        }

        try {
            Files.write(toSave.toPath(), pdfBytes);
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, null, null, getResource("alertFileSaved"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addCreateTestPictureItem() {

        FXMLLoader loader = new FXMLLoader(new ActionItem().getFXML());
        try {
            Node item = loader.load();
            buildItem(loader, getResource("createTestPic"));
            contentPaneOthers.getChildren().add(item);
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass())
                    .atError()
                    .setCause(e)
                    .log("Exception occurred while create item");

            AlertUtils.showErrorAlert(e);
        }
    }

    private Optional<Node> getActionItemById(Node item, String nodeId) {

        return ((Pane) item)
                .getChildren()
                .stream()
                .filter(node -> node.getId().equals(nodeId))
                .findFirst();
    }

    private boolean isItemSelected(Circle selectActionCircle) {

        return selectActionCircle.getFill().equals(done);
    }

    private void checkItemSelected(JFXMasonryPane contentPane) {

        for (Node node : contentPane.getChildren()) {

            Optional<Node> gridPane = getActionItemById(node, "gridPaneContent");
            Optional<Circle> circle = gridPane
                    .flatMap(pane -> getActionItemById(pane, "hBoxCircle"))
                    .flatMap(box -> getActionItemById(box, "selectActionCircle"))
                    .map(Circle.class::cast)
                    .filter(this::isItemSelected);


            if (circle.isPresent()) {
                Label actionLabel = gridPane.flatMap(pane -> getActionItemById(pane, "actionLabel"))
                        .map(Label.class::cast)
                        .orElseThrow();

                final String resultName = actionLabel.getText();

                if (!StringUtils.hasText(resultName)) {
                    AlertUtils.showErrorAlert(getResource("alertChooseTemplate"));
                    return;
                }

                toggleActionButtonVisible(false);

                if (resultName.equals(getResource("createTestPic"))) {
                    this.extractionConnector.createTestImage(this.job.getTemplateName(), this.job.getPathToFile())
                            .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                            .doOnSuccess(v -> Platform.runLater(() -> onTestImage(v)))
                            .doOnSuccess(v -> Platform.runLater(() -> toggleActionButtonVisible(true)))
                            .subscribe();
                } else {
                    this.extractionConnector.runJob(this.job.getTemplateName(), this.job.getPathToFile(), resultName)
                            .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                            .doOnSuccess(v -> Platform.runLater(() -> onSuccess(v)))
                            .doOnSuccess(v -> Platform.runLater(() -> toggleActionButtonVisible(true)))
                            .subscribe();
                }
            }
        }
    }

    private void onTestImage(final Image image) {
        final Dialog<Void> dialog = new Dialog<>();
        final DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
        dialogPane.getChildren().add(new ImageView(image));
        dialogPane.setMinSize(image.getWidth() + 100, image.getHeight());
        dialog.show();
    }
}
