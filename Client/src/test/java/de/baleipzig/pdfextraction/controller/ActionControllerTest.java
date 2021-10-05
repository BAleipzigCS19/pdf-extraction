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
public class ActionControllerTest {

    @Start
    private void start(Stage stage) {

        Parent root = new Parent() {
        };
        try {
            root = FXMLLoader.load(getClass().getResource("/view/ActionView.fxml"));
        } catch (IOException e) {
            System.out.println("Fehler beim Laden der Scene. Fehlermeldung: " + e.getMessage());
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void VerifyViewComponents() {

        FxAssert.verifyThat("#runActionButton", LabeledMatchers.hasText("Aktion durchführen"));
        FxAssert.verifyThat("#backToImportButton", LabeledMatchers.hasText("Zurück"));
        FxAssert.verifyThat("#createTerminationCheckBox", LabeledMatchers.hasText("Kündigung erstellen"));
    }

    @Test
    public void NavigateBack(FxRobot robot) {

        robot.clickOn("#backToImportButton");
        FxAssert.verifyThat("#continueButton", NodeMatchers.isVisible());
    }

    @Test
    public void RunAction(FxRobot robot) {

        robot.clickOn("#runActionButton");
        FxAssert.verifyThat("OK", NodeMatchers.isVisible());
    }
}