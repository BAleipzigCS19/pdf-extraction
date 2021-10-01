package de.baleipzig.pdfextraction.common.alert;

import javafx.scene.control.Alert;

public class AlertCommon {

    public static void ShowAlert(Alert.AlertType alertType, String title, String header, String content) {

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}
