package de.baleipzig.pdfextraction.client.utils.interfaces;

import de.baleipzig.pdfextraction.client.connector.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.PDFRenderer;

public interface InjectableProvider {

    default Object getForField(String fieldName, Class<?> clazz) {
        //Todo: Generallize
        if (clazz == PDFRenderer.class) {
            return PDFRenderer.getInstance();
        }

        if (clazz == TemplateConnector.class) {
            return TemplateConnector.getInstance();
        }

        return customForField(fieldName, clazz);
    }

    default Object customForField(String fieldName, Class<?> clazz) {
        return null;
    }

}
