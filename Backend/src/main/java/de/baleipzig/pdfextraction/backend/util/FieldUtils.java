package de.baleipzig.pdfextraction.backend.util;

import de.baleipzig.pdfextraction.backend.entities.Field;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class FieldUtils {

    private FieldUtils() {
    }

    /**
     * Map's a map of fields to a Map of Strings with the same content
     *
     * @param toMap Map of Fields to map
     * @return Map of Field-Type names to their values
     */
    public static Map<String, String> map(final Map<Field, String> toMap) {
        Objects.requireNonNull(toMap, "Map cannot be null");

        return toMap.keySet()
                .stream()
                .collect(Collectors.toMap(f -> f.getType().name(), toMap::get));
    }
}
