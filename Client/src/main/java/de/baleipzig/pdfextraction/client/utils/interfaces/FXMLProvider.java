package de.baleipzig.pdfextraction.client.utils.interfaces;

import java.net.URL;
import java.util.Objects;

public interface FXMLProvider {
    String DEFAULT_FXML_ENDING = ".fxml";

    /**
     * Provides the Link to the FXML
     *
     * @return The Link to the FXML, never null
     */
    default URL getFXML() {
        return Objects.requireNonNull(getClass().getResource(getClass().getSimpleName() + DEFAULT_FXML_ENDING));
    }

}
