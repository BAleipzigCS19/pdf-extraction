package de.baleipzig.pdfextraction.client;

import de.baleipzig.pdfextraction.common.controller.ControllerUtils;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(final Stage stage) {

        ControllerUtils.switchScene(stage, getClass().getResource("/view/ImportView.fxml"));

    }
}
