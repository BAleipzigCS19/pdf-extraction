package de.baleipzig.pdfextraction.controller;

import de.baleipzig.pdfextraction.common.controller.ControllerUtils;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

@EnabledOnOs({OS.WINDOWS, OS.MAC})
class ImportControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {

        ControllerUtils.switchScene(stage, getClass().getResource("/view/ImportView.fxml"));
    }

    @Test
    void verifyViewComponents() {

        FxAssert.verifyThat("#templateComboBox", NodeMatchers.isVisible());
        FxAssert.verifyThat("#continueButton", LabeledMatchers.hasText("Weiter"));
    }

    @Test
    void navigateContinue() {

        clickOn("#continueButton");
        FxAssert.verifyThat("#backToImportButton", NodeMatchers.isVisible());
    }
}
