package de.baleipzig.pdfextraction.client.utils;

import de.baleipzig.pdfextraction.client.utils.injector.Injector;
import de.baleipzig.pdfextraction.client.view.FXView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class ControllerUtils {

    private static FXView currentView;

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

            currentView = newView;
        } catch (IOException e) {
            AlertUtils.showErrorAlert(e);
            throw new UncheckedIOException(e);
        }

    }


    /**
     * set's the focus, so that not the first element in the scene is autofocused
     * @param control
     */
    public static void changeFocusOnControlParent(Control control){
        Platform.runLater(() -> control.getParent().requestFocus());
    }

    /**
     *
     * @param stage
     */
    public static void reloadScene(Stage stage){
        ControllerUtils.switchScene(stage, currentView);
    }

}
