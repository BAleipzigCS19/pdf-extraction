package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.view.Actions;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.testfx.api.FxAssert.verifyThat;

@EnabledOnOs({OS.WINDOWS, OS.MAC})
class ActionControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {

        ControllerUtils.switchScene(stage, new Actions());
    }

    @Test
    void verifyViewComponents() {

        verifyThat("#runActionButton", LabeledMatchers.hasText("Aktion durchführen"));
        verifyThat("#backToImportButton", LabeledMatchers.hasText("Zurück"));
        verifyThat("#createTerminationCheckBox", LabeledMatchers.hasText("Kündigung erstellen"));
    }

    @Test
    void navigateBack() {

        clickOn("#backToImportButton");
        verifyThat("#continueButton", NodeMatchers.isVisible());
    }

    @Test
    void runAction() {

        clickOn("#runActionButton");
        verifyThat("OK", NodeMatchers.isVisible());
        clickOn("OK");
    }
}
