package de.baleipzig.pdfextraction.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;

@ExtendWith(ApplicationExtension.class)
public class ImportControllerTest {

    @Start
    private void start(Stage stage) {

        Parent root = new Parent() {
        };
        try {
            root = FXMLLoader.load(getClass().getResource("/view/ImportView.fxml"));
        } catch (IOException e) {
            System.out.println("Fehler beim Laden der Scene. Fehlermeldung: " + e.getMessage());
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
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