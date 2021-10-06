package de.baleipzig.pdfextraction.client.utils;

import javafx.scene.control.Alert;

public class AlertUtils {

    private AlertUtils() {}

    public static void showAlert(final Alert.AlertType alertType,
                                 final String title,
                                 final String header,
                                 final String content) {

        getAlert(alertType, title, header, content).show();
    }

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
