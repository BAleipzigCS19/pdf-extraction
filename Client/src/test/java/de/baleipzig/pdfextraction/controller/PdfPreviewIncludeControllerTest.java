package de.baleipzig.pdfextraction.controller;

import de.baleipzig.pdfextraction.common.controller.ControllerUtils;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

@EnabledOnOs({OS.WINDOWS, OS.MAC})
class PdfPreviewIncludeControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {

        ControllerUtils.switchScene(stage, getClass().getResource("/view/include/PdfPreviewInclude.fxml"));
    }

    @Test
    void verifyViewComponents() {

        FxAssert.verifyThat("#pageForwardButton", LabeledMatchers.hasText("->"));
        FxAssert.verifyThat("#pageBackButton", LabeledMatchers.hasText("<-"));
        FxAssert.verifyThat("#chooseFileButton", LabeledMatchers.hasText("Datei Auswählen"));
        FxAssert.verifyThat("#pageIndexLabel", LabeledMatchers.hasText("Seitenanzahl"));
    }
}