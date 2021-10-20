package de.baleipzig.pdfextraction.client.utils.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public interface ResourceBundleProvider {

    String DEFAULT_RB_ENDING = ".properties";

    /**
     * Creates an ResourceBundle with the given Locale.
     *
     * @param locale Locale to load.
     * @return The created ResourcesBundle of the given Locale. Never null
     */
    default ResourceBundle getBundle(Locale locale) {
        final String bundlePath = getClass().getSimpleName() + locale.toLanguageTag() + DEFAULT_RB_ENDING;

        try (final InputStream stream = getClass().getResourceAsStream(bundlePath)) {
            final InputStream actualStream = Optional.ofNullable(stream)
                    .orElse(InputStream.nullInputStream());

            return new PropertyResourceBundle(new InputStreamReader(actualStream, StandardCharsets.UTF_8));
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
