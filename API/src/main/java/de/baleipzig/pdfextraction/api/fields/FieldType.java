package de.baleipzig.pdfextraction.api.fields;

import java.util.EnumSet;
import java.util.Set;

public enum FieldType {

    ADDRESS_RECEIVER("Empfänger-Adresse"),
    ADDRESS_SENDER("Sender-Adresse"),
    EXPIRATION("Ablaufdatum"),
    INSURANCE_NUMBER("Versicherungsnummer");

    private final String name;

    FieldType(String name) {
        this.name = name;
    }

    public static Set<FieldType> getAllFieldTypes() {

        return EnumSet.allOf(FieldType.class);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "FieldType{" +
                "name='" + name + '\'' +
                '}';
    }
}
