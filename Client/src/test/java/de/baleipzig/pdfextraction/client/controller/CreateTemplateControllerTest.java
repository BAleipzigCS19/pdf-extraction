package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.api.fields.FieldType;
import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.utils.PDFRenderer;
import de.baleipzig.pdfextraction.client.view.CreateTemplate;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

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
        verifyThat("#addFieldButton", NodeMatchers.isVisible());
        verifyThat("#createTemplateButton", NodeMatchers.isVisible());
        verifyThat("#cancelButton", NodeMatchers.isVisible());
    }

    @Test
    void createTemplate() throws URISyntaxException {
        PDFRenderer.getInstance().setPdfPath(Path.of(CreateTemplateControllerTest.class.getResource("Kfz-Anschreiben.pdf").toURI()));

        clickOn("#insuranceTextField").write("HUK");
        clickOn("#templateNameTextField").write("HUK-Autoversicherung");
        for (int i = 0; i < FieldType.values().length; i++) {
            clickOn("#addFieldButton").clickOn("OK");
            moveTo("#pdfPreview")
                    .press(MouseButton.PRIMARY)
                    .moveBy(10.0, 10.0)
                    .release(MouseButton.PRIMARY);
        }

        clickOn("Remove");

        clickOn("#addFieldButton")
                .clickOn("OK")
                .moveTo("#pdfPreview")
                .press(MouseButton.PRIMARY)
                .moveBy(-10, -10)
                .release(MouseButton.PRIMARY);

        clickOn("#createTemplateButton").sleep(2, TimeUnit.SECONDS);
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
