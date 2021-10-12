package de.baleipzig.pdfextraction.client.fieldtype;

import de.baleipzig.pdfextraction.api.fields.ApiBase;

public interface FieldTypeBase {

    ApiBase analyze(String text);
}
