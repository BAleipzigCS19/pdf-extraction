package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.utils.SceneHandler;
import de.baleipzig.pdfextraction.client.utils.injector.Injector;
import de.baleipzig.pdfextraction.client.view.PdfPreview;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import java.util.Locale;

@EnabledOnOs({OS.WINDOWS, OS.MAC})
class PdfPreviewIncludeControllerTest extends ApplicationTest {

    @BeforeAll
    static void beforeAll() {
        Locale.setDefault(Locale.GERMAN);
    }

    @Override
    public void start(Stage stage) {
        Injector.createInstance(SceneHandler.class).switchScene(stage, new PdfPreview());
    }

    @Test
    void verifyViewComponents() {

        FxAssert.verifyThat("#pageForwardButton", LabeledMatchers.hasText("->"));
        FxAssert.verifyThat("#pageForwardButton", NodeMatchers.isVisible());
        FxAssert.verifyThat("#pageBackButton", LabeledMatchers.hasText("<-"));
        FxAssert.verifyThat("#pageBackButton", NodeMatchers.isVisible());
        FxAssert.verifyThat("#chooseFileButton", NodeMatchers.isVisible());
        FxAssert.verifyThat("#pageIndexLabel", NodeMatchers.isVisible());
    }
}
