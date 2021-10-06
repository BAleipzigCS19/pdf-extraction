package de.baleipzig.pdfextraction.common.alert;

import javafx.scene.control.Alert;

public class AlertUtils {

    private AlertUtils() {}

    public static void showAlert(Alert.AlertType alertType, String title, String header, String content) {

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}
