package de.baleipzig.pdfextraction.client;

import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.view.ImportView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(final Stage stage) {
        ControllerUtils.switchScene(stage, new ImportView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
