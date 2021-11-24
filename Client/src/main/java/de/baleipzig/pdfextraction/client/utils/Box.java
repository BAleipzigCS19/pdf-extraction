package de.baleipzig.pdfextraction.client.utils;

import de.baleipzig.pdfextraction.api.fields.FieldType;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

public record Box(int page, FieldType type, Rectangle place, Paint color) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Box other)) {
            return false;
        }
        return page == other.page
                && type == other.type
                && Objects.equals(place, other.place)
                && Objects.equals(color, other.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, type, place, color);
    }
}
