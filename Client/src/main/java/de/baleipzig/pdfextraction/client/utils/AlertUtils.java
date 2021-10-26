package de.baleipzig.pdfextraction.client.utils;

import javafx.scene.control.Alert;

import java.util.Optional;

public class AlertUtils {

    private AlertUtils() {
    }

    /**
     * Creates an Alert with the given Information and instantly displays it.
     *
     * @param alertType The Type of Altert see {@link javafx.scene.control.Alert.AlertType}
     * @param title     The Title of the Window
     * @param header    Text above the main Message, Nullable
     * @param content   The main Message
     */
    public static void showAlert(final Alert.AlertType alertType,
                                 final String title,
                                 final String header,
                                 final String content) {

        getAlert(alertType, title, header, content).show();
    }

    /**
     * Creates an Error Alter with the given Throwable
     *
     * @param t Optional Exception that caused this Alert
     */
    public static void showErrorAlert(final Throwable t) {
        final String errorMessage = Optional.ofNullable(t)
                .map(Throwable::getLocalizedMessage)
                .orElse("");

        showErrorAlert(errorMessage);
    }

    /**
     * Creates an Error Alter with the given Throwable
     *
     * @param errorMessage Additional Error-Message
     */
    public static void showErrorAlert(final String errorMessage) {
        showAlert(Alert.AlertType.ERROR, "Fehler", null, "Ein Fehler ist aufgetreten.\n" + errorMessage);
    }

    /**
     * Creates an Alert with the given Information for further customizations
     *
     * @param alertType The Type of Altert see {@link javafx.scene.control.Alert.AlertType}
     * @param title     The Title of the Window
     * @param header    Text above the main Message, Nullable
     * @param content   The main Message
     * @return The created Alert
     */
    public static Alert getAlert(final Alert.AlertType alertType,
                                 final String title,
                                 final String header,
                                 final String content) {
        final Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }
}
