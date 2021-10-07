package de.baleipzig.pdfextraction.fieldtype;

import java.util.Arrays;
import java.util.List;

public enum FieldTypes {

    ADDRESS_RECEIVER("Empfänger-Adresse"),
    ADDRESS_SENDER("Sender-Adresse"),
    EXPIRATION("Ablaufdatum"),
    INSURANCE_NUMBER("Versicherungsnummer");

    private String name;

    FieldTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<FieldTypes> getAllFieldTypes(){

        return Arrays.asList(FieldTypes.values());
    }
}
