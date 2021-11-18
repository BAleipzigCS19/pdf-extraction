package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.api.dto.FieldDTO;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.api.fields.FieldType;
import de.baleipzig.pdfextraction.client.connector.api.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.SceneHandler;
import de.baleipzig.pdfextraction.client.utils.injector.Injector;
import de.baleipzig.pdfextraction.client.view.Imports;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.testfx.api.FxAssert.verifyThat;

@EnabledOnOs({OS.WINDOWS, OS.MAC})
class ImportControllerTest extends ApplicationTest {

    private TemplateConnector connector;

    @BeforeAll
    static void beforeAll() {
        Locale.setDefault(Locale.GERMAN);
    }

    @Override
    public void start(Stage stage) {
        Injector.createInstance(SceneHandler.class).switchScene(stage, new Imports());
    }

    @Test
    void verifyViewComponents() {

        verifyThat("#templateComboBox", NodeMatchers.isVisible());
        verifyThat("#continueButton", NodeMatchers.isVisible());
        verifyThat("#pageForwardButton", LabeledMatchers.hasText(">"));
        verifyThat("#pageForwardButton", NodeMatchers.isVisible());
        verifyThat("#pageBackButton", LabeledMatchers.hasText("<"));
        verifyThat("#pageBackButton", NodeMatchers.isVisible());
        verifyThat("#pageIndexLabel", NodeMatchers.isVisible());
        verifyThat("#showTemplateButton", NodeMatchers.isVisible());
    }

    @Test
    @Disabled("Without choosing a Template and an PDF it can't continue.")
    void navigateContinue() {

        clickOn("#continueButton");
        verifyThat("#backToImportButton", NodeMatchers.isVisible());
    }



}
