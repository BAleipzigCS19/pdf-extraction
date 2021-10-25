package de.baleipzig.pdfextraction.backend.tesseract;

import de.baleipzig.pdfextraction.backend.entities.Field;
import de.baleipzig.pdfextraction.backend.entities.Template;

import java.util.Map;

public interface Tess {

    Map<Field, String> doOCR(final Template template, final byte[] fileContent);
}
