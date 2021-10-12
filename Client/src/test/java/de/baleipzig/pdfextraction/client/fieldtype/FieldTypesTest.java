package de.baleipzig.pdfextraction.client.fieldtype;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldTypesTest {

    @Test
    void getAllFieldTypesTest() {

        List<FieldTypes> expectedFieldTypes = Arrays.asList(
                FieldTypes.ADDRESS_SENDER,
                FieldTypes.ADDRESS_RECEIVER,
                FieldTypes.EXPIRATION,
                FieldTypes.INSURANCE_NUMBER
        );

        final List<FieldTypes> actual = FieldTypes.getAllFieldTypes();
        assertTrue(actual.containsAll(expectedFieldTypes));
        assertTrue(expectedFieldTypes.containsAll(actual));
    }
}
