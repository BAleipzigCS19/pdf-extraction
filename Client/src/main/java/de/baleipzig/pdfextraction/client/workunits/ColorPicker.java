package de.baleipzig.pdfextraction.client.workunits;

import de.baleipzig.pdfextraction.client.controller.CreateTemplateController;
import de.baleipzig.pdfextraction.client.utils.Box;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ColorPicker {

    private final List<Color> freeToUse;

    public ColorPicker(Set<Box> existingBoxes) {

        Set<Paint> usedColors = existingBoxes.stream()
                .map(Box::color)
                .collect(Collectors.toSet());

        freeToUse = Stream.of(Color.CRIMSON, Color.DARKGREEN, Color.MEDIUMBLUE, Color.DEEPPINK, Color.GREEN, Color.INDIGO, Color.RED)
                .filter(Predicate.not(usedColors::contains))
                .toList();
    }

    public Paint getColor() {
        
        if (freeToUse.isEmpty()) {
            return Color.BLACK;//Default
        }

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        return freeToUse.get(random.nextInt(0, freeToUse.size() - 1));
    }
}
