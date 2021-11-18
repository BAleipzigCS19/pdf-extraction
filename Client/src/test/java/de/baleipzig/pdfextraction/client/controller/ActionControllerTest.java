package de.baleipzig.pdfextraction.client.controller;

import com.jfoenix.controls.JFXMasonryPane;
import de.baleipzig.pdfextraction.client.utils.SceneHandler;
import de.baleipzig.pdfextraction.client.utils.injector.Injector;
import de.baleipzig.pdfextraction.client.view.Actions;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import java.util.Locale;

import static org.testfx.api.FxAssert.verifyThat;

@EnabledOnOs({OS.WINDOWS, OS.MAC})
class ActionControllerTest extends ApplicationTest {

    private Stage stage;

    @BeforeAll
    static void beforeAll() {
        Locale.setDefault(Locale.GERMAN);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Injector.createInstance(SceneHandler.class).switchScene(stage, new Actions());
    }

    @Test
    void verifyViewComponents() {

        verifyThat("#runActionButton", NodeMatchers.isVisible());
        verifyThat("#backToImportButton", NodeMatchers.isVisible());
    }

    @Test
    void navigateBack() {

        clickOn("#backToImportButton");
        verifyThat("#continueButton", NodeMatchers.isVisible());
    }

    @Test
    void selectMoreThanOneAction() {

        ObservableList<Node> items = ((JFXMasonryPane)(stage.getScene().lookup("#contentPane"))).getChildren();
        clickOn(items.get(0).lookup("#selectActionCircle"));
        clickOn(items.get(1).lookup("#selectActionCircle"));
        clickOn("#runActionButton");

        verifyThat("Es darf nur eine Aktion ausgewählt werden.",NodeMatchers.isVisible());
    }

    @Test
    @Disabled("Without choosing a Template and an PDF it can't continue.")
    void runAction() {
        clickOn("#runActionButton");
        verifyThat("OK", NodeMatchers.isVisible());
        clickOn("OK");
    }
}
