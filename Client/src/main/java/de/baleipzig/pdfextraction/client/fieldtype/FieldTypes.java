package de.baleipzig.pdfextraction.client.fieldtype;

import java.util.Arrays;
import java.util.List;

public enum FieldTypes {

    ADDRESS_RECEIVER("Empfänger-Adresse"),
    ADDRESS_SENDER("Sender-Adresse"),
    EXPIRATION("Ablaufdatum"),
    INSURANCE_NUMBER("Versicherungsnummer");

    private final String name;

    FieldTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<FieldTypes> getAllFieldTypes(){

        return Arrays.asList(FieldTypes.values());
    }
}
