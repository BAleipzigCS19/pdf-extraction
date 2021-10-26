package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.view.Imports;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.testfx.api.FxAssert.verifyThat;

@EnabledOnOs({OS.WINDOWS, OS.MAC})
class ImportControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        ControllerUtils.switchScene(stage, new Imports());
    }

    @Test
    void verifyViewComponents() {

        verifyThat("#templateComboBox", NodeMatchers.isVisible());
        verifyThat("#continueButton", NodeMatchers.isVisible());
        verifyThat("#pageForwardButton", LabeledMatchers.hasText("->"));
        verifyThat("#pageForwardButton", NodeMatchers.isVisible());
        verifyThat("#pageBackButton", LabeledMatchers.hasText("<-"));
        verifyThat("#pageBackButton", NodeMatchers.isVisible());
        verifyThat("#chooseFileButton", NodeMatchers.isVisible());
        verifyThat("#pageIndexLabel", NodeMatchers.isVisible());
    }

    @Test
    @Disabled("Without choosing a Template and an PDF it can't continue.")
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
