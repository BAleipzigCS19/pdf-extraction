package de.baleipzig.pdfextraction.backend.util;

import de.baleipzig.pdfextraction.api.dto.FieldDTO;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.backend.entities.Field;

import java.util.List;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    /**
     * Checks if the given DTO seems plausible
     *
     * @param dto DTO to check
     * @return true if the DTO seems plausible, else false
     */
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

    /**
     * Checks if the given DTO seems plausible
     *
     * @param field DTO to check
     * @return true if the DTO seems plausible, else false
     */
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

    /**
     * Maps the list of Fields to a List of Field-DTO's with {@link #mapToFieldDTO(Field)}
     *
     * @param fields List of fields to map
     * @return List of each mapped Field-DTO's
     */
    public static List<FieldDTO> mapToFieldDTO(List<Field> fields) {
        return fields.stream()
                .map(ValidationUtils::mapToFieldDTO)
                .toList();
    }

    /**
     * Map's a single Field to their DTO
     *
     * @param f Field to map
     * @return The Field-DTO with the information of the given Field
     */
    public static FieldDTO mapToFieldDTO(Field f) {
        return new FieldDTO(f.getType(), f.getPage(), f.getxPosPercentage(), f.getyPosPercentage(),
                f.getWidthPercentage(), f.getHeightPercentage());
    }

    /**
     * Maps the list of Fields-DTO's to a List of Field with {@link #mapToField(FieldDTO)}
     *
     * @param fields List of fields to map
     * @return List of each mapped Field
     */
    public static List<Field> mapToField(List<FieldDTO> fields) {
        return fields.stream()
                .map(ValidationUtils::mapToField)
                .toList();
    }

    /**
     * Map's a single DTO Field to their Field
     *
     * @param f Field-DTO to map
     * @return The Field with the information of the given Field-DTO
     */
    public static Field mapToField(final FieldDTO f) {
        return new Field(f.getType(), f.getPage(), f.getxPosPercentage(), f.getyPosPercentage(),
                f.getWidthPercentage(), f.getHeightPercentage());
    }
}
