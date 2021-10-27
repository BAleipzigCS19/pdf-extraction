package de.baleipzig.pdfextraction.backend.tesseract;

import de.baleipzig.pdfextraction.backend.entities.Field;

import java.util.Collection;
import java.util.Map;

public interface Tess {

    Map<Field, String> doBatchOCR(final Collection<Field> template, final byte[] fileContent);
}
