package de.baleipzig.pdfextraction.controller;

import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.view.CreateTemplate;
import de.baleipzig.pdfextraction.fieldtype.FieldTypes;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

@EnabledOnOs({OS.WINDOWS, OS.MAC})
class CreateTemplateControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {

        ControllerUtils.switchScene(stage, new CreateTemplate());
    }

    @Test
    void verifyViewComponents() {

        FxAssert.verifyThat("#insuranceTextField", NodeMatchers.isVisible());
        FxAssert.verifyThat("#templateNameTextField", NodeMatchers.isVisible());
        FxAssert.verifyThat("#addFieldButton", LabeledMatchers.hasText("+"));
        FxAssert.verifyThat("#createTemplateButton", LabeledMatchers.hasText("Template erstellen"));
    }

    @Test
    void createTemplate() {

        clickOn("#insuranceTextField").write("HUK");
        clickOn("#templateNameTextField").write("HUK-Autoversicherung");
        for (int i = 0; i < FieldTypes.values().length; i++) {
            clickOn("#addFieldButton").clickOn("OK");
        }

        clickOn("#createTemplateButton");
        clickOn("OK");
        FxAssert.verifyThat("#continueButton", NodeMatchers.isVisible());
    }

    @Test
    void dataIsIncomplete() {
        clickOn("#insuranceTextField").write("");
        clickOn("#templateNameTextField").write("HUK-Autoversicherung");
        clickOn("#createTemplateButton").clickOn("OK");

        FxAssert.verifyThat("#createTemplateButton", NodeMatchers.isVisible());
    }
}
