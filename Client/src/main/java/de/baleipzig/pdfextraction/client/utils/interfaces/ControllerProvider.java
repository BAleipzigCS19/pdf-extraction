package de.baleipzig.pdfextraction.client.utils.interfaces;

import de.baleipzig.pdfextraction.client.utils.ControllerUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public interface ControllerProvider {

    default Set<Field> getAllController() {
        return Arrays.stream(getClass()
                        .getDeclaredFields())
                .filter(f -> Controller.class.isAssignableFrom(f.getType()))
                .collect(Collectors.toSet());
    }

    default Controller getControllerForType(final Class<?> type) {
        return Arrays.stream(getClass().getDeclaredFields())
                .filter(f -> f.getType() == type)
                .findFirst()
                .map(f -> {
                            try {
                                return ControllerUtils.getFromField(this, f);
                            } catch (IllegalAccessException e) {
                                return null;
                            }
                        }

                )
                .map(Controller.class::cast)
                .orElseGet(() -> customController(type));
    }

    default Controller customController(Class<?> type) {
        return null;
    }
}
