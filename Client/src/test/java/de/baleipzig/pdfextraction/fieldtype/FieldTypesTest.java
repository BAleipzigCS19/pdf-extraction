package de.baleipzig.pdfextraction.fieldtype;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FieldTypesTest {

    @Test
    void getAllFieldTypesTest() {

        List<FieldTypes> fieldTypes = FieldTypes.getAllFieldTypes();

        assertNotNull(fieldTypes);
        assertEquals(4, fieldTypes.size());
        assertEquals("Empfänger-Adresse", fieldTypes.get(0).getName());
        assertEquals("Sender-Adresse", fieldTypes.get(1).getName());
        assertEquals("Ablaufdatum", fieldTypes.get(2).getName());
        assertEquals("Versicherungsnummer", fieldTypes.get(3).getName());
    }
}