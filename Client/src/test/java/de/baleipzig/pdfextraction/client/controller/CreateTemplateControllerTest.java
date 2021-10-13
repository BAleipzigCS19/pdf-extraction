package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.view.CreateTemplate;
import de.baleipzig.pdfextraction.client.fieldtype.FieldTypes;
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
class CreateTemplateControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {

        ControllerUtils.switchScene(stage, new CreateTemplate());
    }

    @Test
    void verifyViewComponents() {

        verifyThat("#insuranceTextField", NodeMatchers.isVisible());
        verifyThat("#templateNameTextField", NodeMatchers.isVisible());
        verifyThat("#addFieldButton", LabeledMatchers.hasText("+"));
        verifyThat("#createTemplateButton", LabeledMatchers.hasText("Template erstellen"));
        verifyThat("#cancelButton", LabeledMatchers.hasText("Abbrechen"));
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
        verifyThat("#continueButton", NodeMatchers.isVisible());
    }

    @Test
    void dataIsIncomplete() {
        clickOn("#insuranceTextField").write("");
        clickOn("#templateNameTextField").write("HUK-Autoversicherung");
        clickOn("#createTemplateButton").clickOn("OK");

        verifyThat("#createTemplateButton", NodeMatchers.isVisible());
    }

    @Test
    void cancelCreateTemplate() {

        clickOn("#cancelButton");
        verifyThat("#continueButton", NodeMatchers.isVisible());
    }
}
