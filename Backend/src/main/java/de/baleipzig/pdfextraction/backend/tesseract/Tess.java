package de.baleipzig.pdfextraction.backend.tesseract;

import de.baleipzig.pdfextraction.backend.entities.Field;

import java.util.Collection;
import java.util.Map;

public interface Tess {

    /**
     * Attempts to extract the given Fields from the given PDF with Tesseract
     *
     * @param fields      Fields to Extract
     * @param fileContent PDF file
     * @return Map of Fields to the Content that was extracted
     */
    Map<Field, String> doBatchOCR(final Collection<Field> fields, final byte[] fileContent);
}
