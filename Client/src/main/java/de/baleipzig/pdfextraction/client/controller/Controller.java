package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.utils.LanguageHandler;
import de.baleipzig.pdfextraction.client.utils.SceneHandler;
import de.baleipzig.pdfextraction.client.view.FXView;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.scene.control.Control;
import javafx.stage.Stage;


abstract class Controller {

    @Inject
    private SceneHandler sceneHandler;

    @Inject
    private LanguageHandler languageHandler;

    protected String getResource(String key) {
        return languageHandler.getCurrentBundle().getString(key);
    }

    protected void switchScene(Stage current, FXView view) {
        this.sceneHandler.switchScene(current, view);
    }

    protected void reloadScene(Stage current) {
        sceneHandler.reloadScene(current);
    }

    /**
     * set's the focus, so that not the first element in the scene is autofocused
     *
     * @param control The GUI element on which parent the focus is set
     */
    protected void changeFocusOnControlParent(Control control) {
        Platform.runLater(() -> control.getParent().requestFocus());
    }

    protected LanguageHandler getLanguageHandler() {
        return this.languageHandler;
    }
}
