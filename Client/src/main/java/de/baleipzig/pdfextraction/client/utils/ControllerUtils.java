package de.baleipzig.pdfextraction.client.utils;

import de.baleipzig.pdfextraction.client.utils.interfaces.InjectableProvider;
import de.baleipzig.pdfextraction.client.view.FXView;
import jakarta.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

public final class ControllerUtils {

    private ControllerUtils() {
    }

    /**
     * Switches from the current Scene over to the new Scene
     *
     * @param current The current Stage
     * @param newView The Scene to switch to
     */
    public static <T extends FXView> void switchScene(final Stage current, final T newView) {
        try {
            //todo: Instantiate via Class and Controllerfactory
            injectInFields(newView);

            final Parent parent = FXMLLoader.load(newView.getFXML(), newView.getBundle(Locale.getDefault()), null, newView::getControllerForType, StandardCharsets.UTF_8);
            final Scene scene = new Scene(parent);
            current.setScene(scene);
            current.show();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (IllegalAccessException e) {
            LoggerFactory.getLogger(ControllerUtils.class)
                    .atError()
                    .setCause(e)
                    .addArgument(() -> newView.getClass().getName())
                    .log("Exception while injecting for View {}");
        }
    }

    private static <T extends FXView> void injectInFields(T newView)
            throws IllegalAccessException {
        for (final Field c : newView.getAllController()) {

            final Object toCheck = getFromField(newView, c);

            final Collection<Field> injectableFields = getInjectableFields(toCheck);
            if (injectableFields.isEmpty()) {
                continue;
            }
            for (final Field field : injectableFields) {

                final Object toInject = newView.getForField(field.getName(), field.getType());

                if (toInject == null) {
                    throw new IllegalArgumentException("Provider %s provided no value for field %s".formatted(newView.getClass().getName(), field));
                }
                doInjection(toCheck, newView, field);
            }
        }
    }

    private static void doInjection(Object instance, Object toSet, Field field)
            throws IllegalAccessException {
        final boolean wasAccessible = field.canAccess(instance);
        try {
            if (!wasAccessible) {
                field.setAccessible(true);
            }

            field.set(instance, toSet);
        } finally {
            if (!wasAccessible) {
                field.setAccessible(false);
            }
        }
    }

    public static Object getFromField(Object instance, Field c)
            throws IllegalAccessException {
        final Object toCheck;
        final boolean wasAccessible = c.canAccess(instance);
        try {
            if (!wasAccessible) {
                c.setAccessible(true);
            }

            toCheck = c.get(instance);
        } finally {
            if (!wasAccessible) {
                c.setAccessible(false);
            }
        }
        return toCheck;
    }

    private static Collection<Field> getInjectableFields(final Object toCheck) {
        return Arrays.stream(toCheck.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Inject.class) != null)
                .toList();
    }
}
