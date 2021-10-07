package de.baleipzig.pdfextraction.fieldtype;

import de.baleipzig.pdfextraction.api.ApiBase;

public interface FieldTypeBase {

    ApiBase analyze(String text);
}
