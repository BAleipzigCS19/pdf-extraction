package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.utils.SceneHandler;
import de.baleipzig.pdfextraction.client.utils.injector.Injector;
import de.baleipzig.pdfextraction.client.view.Actions;
import de.baleipzig.pdfextraction.client.view.Imports;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import java.util.Locale;

import static org.testfx.api.FxAssert.verifyThat;

@EnabledOnOs({OS.WINDOWS, OS.MAC})
class MenuBarControllerTest extends ApplicationTest {

    @BeforeAll
    static void beforeAll() {
        Locale.setDefault(Locale.GERMAN);
    }

    @Override
    public void start(Stage stage) {
        Injector.createInstance(SceneHandler.class).switchScene(stage, new Imports());
    }

    @Test
    void verifyComponents() {
        verifyThat("#menuBar", NodeMatchers.isVisible());
        verifyThat("#fileMenu", NodeMatchers.isVisible());
        verifyThat("#templateMenuID", NodeMatchers.isVisible());
        verifyThat("#languageMenuID", NodeMatchers.isVisible());
    }

    @Test
    void verifyFileMenu() {
        clickOn("#fileMenu");
        verifyThat("#chooseFile", NodeMatchers.isVisible());
    }

    @Test
    void verifyTemplateMenu() {
        clickOn("#templateMenuID");
        verifyThat("#createTemplate", NodeMatchers.isVisible());
    }

    @Test
    void verifyLanguageMenu() {
        clickOn("#languageMenuID");
        verifyThat("#deutsch", NodeMatchers.isVisible());
        verifyThat("#english", NodeMatchers.isVisible());
    }

    @Test
    void navigateCreateTemplate() {
        clickOn("#templateMenuID");
        clickOn("#createTemplate");
        verifyThat("#addFieldButton", NodeMatchers.isVisible());
        clickOn("#cancelButton");
        verifyThat("#templateComboBox", NodeMatchers.isVisible());
    }

    @Test
    void changeLangauage() {
        verifyThat("#templateHeader", LabeledMatchers.hasText("Schablonen"));
        clickOn("#languageMenuID");
        clickOn("#english");
        verifyThat("#templateHeader", LabeledMatchers.hasText("Templates"));
    }

}
