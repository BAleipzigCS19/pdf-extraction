package de.baleipzig.pdfextraction.client.utils;

import de.baleipzig.pdfextraction.client.utils.injector.Injector;
import de.baleipzig.pdfextraction.client.view.FXView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class ControllerUtils {

    private ControllerUtils() {
    }

    /**
     * Switches from the current Scene over to the new Scene
     *
     * @param current The current Stage
     * @param newView The Scene to switch to
     */
    public static <T extends FXView> void switchScene(final Stage current, final T newView) {
        try {
            final Parent parent = FXMLLoader.load(newView.getFXML(), newView.getBundle(Locale.getDefault()), null, Injector::createInstance, StandardCharsets.UTF_8);
            final Scene scene = new Scene(parent);
            current.setScene(scene);
            current.show();
        } catch (IOException e) {
            AlertUtils.showErrorAlert(e);
            throw new UncheckedIOException(e);
        }
    }

}
