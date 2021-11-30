package de.baleipzig.pdfextraction.api.fields;

import java.util.EnumSet;
import java.util.Set;

public enum FieldType {

    ADDRESS_RECEIVER("Empfänger-Adresse", "ADDRESS_RECEIVER"),
    ADDRESS_SENDER("Sender-Adresse", "ADDRESS_SENDER"),
    EXPIRATION("Ablaufdatum", "EXPIRATION"),
    INSURANCE_NUMBER("Versicherungsnummer", "INSURANCE_NUMBER");

    private final String name;
    private final String internName;

    FieldType(String name, String internName) {
        this.name = name;
        this.internName = internName;
    }

    public static Set<FieldType> getAllFieldTypes() {

        return EnumSet.allOf(FieldType.class);
    }

    public String getName() {
        return name;
    }

    public String getInternName() {
        return internName;
    }

    @Override
    public String toString() {
        return "FieldType{" +
                "name='" + name + '\'' +
                ", internName='" + internName + '\'' +
                '}';
    }
}
