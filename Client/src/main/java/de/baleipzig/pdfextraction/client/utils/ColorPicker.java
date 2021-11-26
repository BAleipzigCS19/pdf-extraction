package de.baleipzig.pdfextraction.client.utils;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ColorPicker(Set<Box> existingBoxes) {

    public Paint getColor() {
        final Set<Paint> usedColors = existingBoxes.stream()
                .map(Box::color)
                .collect(Collectors.toSet());

        final List<Color> freeToUse = Stream.of(Color.CRIMSON, Color.DARKGREEN, Color.MEDIUMBLUE, Color.DEEPPINK, Color.GREEN, Color.INDIGO, Color.RED)
                .filter(Predicate.not(usedColors::contains))
                .toList();

        if (freeToUse.isEmpty()) {
            return Color.BLACK;//Default
        }

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        return freeToUse.get(random.nextInt(0, freeToUse.size() - 1));
    }
}
