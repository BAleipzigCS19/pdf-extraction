package de.baleipzig.pdfextraction.controller;

import de.baleipzig.pdfextraction.base.TestBaseUtils;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

@ExtendWith(ApplicationExtension.class)
public class ActionControllerTest {

    @Start
    private void start(Stage stage) {

        TestBaseUtils.start(stage, getClass().getResource("/view/ActionView.fxml"));
    }

    @Test
    public void verifyViewComponents() {

        FxAssert.verifyThat("#runActionButton", LabeledMatchers.hasText("Aktion durchführen"));
        FxAssert.verifyThat("#backToImportButton", LabeledMatchers.hasText("Zurück"));
        FxAssert.verifyThat("#createTerminationCheckBox", LabeledMatchers.hasText("Kündigung erstellen"));
    }

    @Test
    public void navigateBack(FxRobot robot) {

        robot.clickOn("#backToImportButton");
        FxAssert.verifyThat("#continueButton", NodeMatchers.isVisible());
    }

    @Test
    public void runAction(FxRobot robot) {

        robot.clickOn("#runActionButton");
        FxAssert.verifyThat("OK", NodeMatchers.isVisible());
        robot.clickOn("OK");
    }
}