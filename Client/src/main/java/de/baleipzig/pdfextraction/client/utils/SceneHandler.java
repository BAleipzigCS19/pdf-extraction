package de.baleipzig.pdfextraction.client.utils;

import de.baleipzig.pdfextraction.client.utils.injector.Injector;
import de.baleipzig.pdfextraction.client.view.FXView;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;


@Singleton
public class SceneHandler {

    @Inject
    LanguageHandler languageHandler;

    private FXView currentView;

    /**
     * Switches from the current Scene over to the new Scene
     *
     * @param current The current Stage
     * @param newView The Scene to switch to
     */
    public <T extends FXView> void switchScene(final Stage current, final T newView) {
        try {
            final Parent parent = FXMLLoader.load(newView.getFXML(), this.languageHandler.getCurrentBundle(), null, Injector::createInstance, StandardCharsets.UTF_8);
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
     * reloads the current Scene
     * @param stage The current Stage
     */
    public void reloadScene(Stage stage){
        switchScene(stage, currentView);
    }
}
