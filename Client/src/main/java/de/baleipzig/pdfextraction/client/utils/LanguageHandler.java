package de.baleipzig.pdfextraction.client.utils;

import jakarta.inject.Singleton;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

@Singleton
public class LanguageHandler {

    static final String DEFAULT_RB_ENDING = ".properties";
    private ResourceBundle currentBundle;

    /**
     * Creates an ResourceBundle with the given Locale.
     *
     * @param locale Locale to load.
     * @return The created ResourcesBundle of the given Locale. Never null
     */
    private ResourceBundle loadBundle(Locale locale) {
        final String bundlePath = "../view/languages/language_" + locale.toLanguageTag() + DEFAULT_RB_ENDING;

        try (final InputStream stream = SceneHandler.class.getResourceAsStream(bundlePath)) {
            final InputStream actualStream = Optional.ofNullable(stream)
                    .orElse(InputStream.nullInputStream());

            return new PropertyResourceBundle(new InputStreamReader(actualStream, StandardCharsets.UTF_8));
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public ResourceBundle getCurrentBundle() {
        final boolean isEqual = Optional.ofNullable(currentBundle)
                // das ist bloß ein Cast auf die Locale wenn current Bundle nicht null ist
                .map(ResourceBundle::getLocale)
                // nach dem Cast wird geprüft ob die Locale in current Bundle schon die Default Locale ist
                .map(Locale.getDefault()::equals)
                // wenn das current Bundle null ist kommt direkt false zurück
                .orElse(false);
        // ist current Bundle == null oder ist nicht equal der Default Locale wird das entsprechende Ressource Bundle geladen
        if (!isEqual){
            currentBundle = loadBundle(Locale.getDefault());
        }
        return currentBundle;
    }
}
