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

import static org.testfx.api.FxAssert.verifyThat;

@EnabledOnOs({OS.WINDOWS, OS.MAC})
class ImportControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {

        ControllerUtils.switchScene(stage, getClass().getResource("/view/ImportView.fxml"));
    }

    @Test
    void verifyViewComponents() {

        verifyThat("#templateComboBox", NodeMatchers.isVisible());
        verifyThat("#continueButton", LabeledMatchers.hasText("Weiter"));
        verifyThat("#buttonPageForward", LabeledMatchers.hasText("->"));
        verifyThat("#buttonPageBack", LabeledMatchers.hasText("<-"));
        verifyThat("#buttonChooseFile", LabeledMatchers.hasText("Datei Auswählen"));
        verifyThat("#pageIndex", NodeMatchers.isVisible());
    }

    @Test
    void navigateContinue() {

        clickOn("#continueButton");
        verifyThat("#backToImportButton", NodeMatchers.isVisible());
    }

    @Test
    void navigateCreateTemplate() {

        clickOn("#createTemplateButton");
        verifyThat("#addFieldButton", NodeMatchers.isVisible());
    }
}
