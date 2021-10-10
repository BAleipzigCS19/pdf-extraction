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
        assertTrue(fieldTypes.contains(FieldTypes.ADDRESS_SENDER));
        assertTrue(fieldTypes.contains(FieldTypes.ADDRESS_RECEIVER));
        assertTrue(fieldTypes.contains(FieldTypes.EXPIRATION));
        assertTrue(fieldTypes.contains(FieldTypes.INSURANCE_NUMBER));
    }
}