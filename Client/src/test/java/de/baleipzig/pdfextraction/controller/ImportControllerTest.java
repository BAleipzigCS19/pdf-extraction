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
public class ImportControllerTest {

    @Start
    private void start(Stage stage) {

        TestBaseUtils.start(stage, getClass().getResource("/view/ImportView.fxml"));
    }

    @Test
    public void verifyViewComponents() {

        FxAssert.verifyThat("#templateComboBox", NodeMatchers.isVisible());
        FxAssert.verifyThat("#continueButton", LabeledMatchers.hasText("Weiter"));
    }

    @Test
    public void navigateContinue(FxRobot robot) {

        robot.clickOn("#continueButton");
        FxAssert.verifyThat("#backToImportButton", NodeMatchers.isVisible());
    }
}