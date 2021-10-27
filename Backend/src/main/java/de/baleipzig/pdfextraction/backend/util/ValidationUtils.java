package de.baleipzig.pdfextraction.backend.util;

import de.baleipzig.pdfextraction.api.dto.FieldDTO;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.backend.entities.Field;

import java.util.List;

public final class ValidationUtils {

    public static boolean isValidDTO(final TemplateDTO dto) {
        final boolean areFieldsValid = dto != null && dto.getName() != null && !dto.getName().isBlank()
                && dto.getConsumer() != null && !dto.getConsumer().isBlank()
                && dto.getFields() != null && !dto.getFields().isEmpty();
        if (!areFieldsValid) {
            return false;
        }

        for (final FieldDTO field : dto.getFields()) {
            if (!isFieldDTOValid(field)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFieldDTOValid(FieldDTO field) {
        if (field.getPage() < 0) {
            return false;
        }

        if (field.getWidthPercentage() < 0 || field.getWidthPercentage() > 1) {
            return false;
        }

        if (field.getHeightPercentage() < 0 || field.getHeightPercentage() > 1) {
            return false;
        }

        if (field.getxPosPercentage() < 0 || field.getxPosPercentage() > 1) {
            return false;
        }

        if (field.getyPosPercentage() < 0 || field.getyPosPercentage() > 1) {
            return false;
        }

        if (field.getxPosPercentage() + field.getWidthPercentage() > 1) {
            return false;
        }

        return field.getyPosPercentage() + field.getHeightPercentage() <= 1;
    }

    public static List<FieldDTO> mapToFieldDTO(List<Field> fields) {
        return fields.stream()
                .map(ValidationUtils::mapToFieldDTO)
                .toList();
    }

    public static FieldDTO mapToFieldDTO(Field f) {
        return new FieldDTO(f.getType(), f.getPage(), f.getxPosPercentage(), f.getyPosPercentage(),
                f.getWidthPercentage(), f.getHeightPercentage());
    }

    public static List<Field> mapToField(List<FieldDTO> fields) {
        return fields.stream()
                .map(ValidationUtils::mapToField)
                .toList();
    }

    public static Field mapToField(final FieldDTO f) {
        return new Field(f.getType(), f.getPage(), f.getxPosPercentage(), f.getyPosPercentage(),
                f.getWidthPercentage(), f.getHeightPercentage());
    }

    private ValidationUtils() {
    }
}
