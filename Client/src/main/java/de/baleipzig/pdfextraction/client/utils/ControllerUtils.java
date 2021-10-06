package de.baleipzig.pdfextraction.client.utils;

import de.baleipzig.pdfextraction.client.utils.views.FXMLView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ControllerUtils {

    private ControllerUtils() {
    }

    public static void switchScene(final Stage current, final FXMLView newView) {
        final Scene scene = new Scene(newView.getView());
        current.setScene(scene);
        current.show();
    }
}
