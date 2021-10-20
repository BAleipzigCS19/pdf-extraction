package de.baleipzig.pdfextraction.client;

import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.view.Imports;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) {
        ControllerUtils.switchScene(stage, new Imports());
    }
}
