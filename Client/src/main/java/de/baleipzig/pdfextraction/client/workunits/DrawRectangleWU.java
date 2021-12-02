package de.baleipzig.pdfextraction.client.workunits;

import de.baleipzig.pdfextraction.api.dto.FieldDTO;
import de.baleipzig.pdfextraction.client.utils.Box;
import de.baleipzig.pdfextraction.client.utils.ColorPicker;
import de.baleipzig.pdfextraction.client.utils.Size;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DrawRectangleWU {
    private final ImageView imageView;
    private final List<FieldDTO> fieldDTOS;
    private final ColorPicker picker;

    public DrawRectangleWU(ImageView imageView, List<FieldDTO> fieldDTOS) {
        this(imageView, fieldDTOS, new HashSet<>());
    }

    public DrawRectangleWU(ImageView imageView, List<FieldDTO> fieldDTOS, Set<Box> templateBoxes) {
        this.imageView = imageView;
        this.fieldDTOS = fieldDTOS;
        this.picker = new ColorPicker(templateBoxes);
    }

    /**
     * Creates rectangles out of the in the constructor loaded TemplateDTO, that can be added on a container
     *
     * @return a List of javafx Rectangles
     */
    public Set<Box> work() {
        final Set<Box> result = new HashSet<>(fieldDTOS.size());
        for (FieldDTO field : fieldDTOS) {
            final Rectangle rectangle = getRectangle(getSize(imageView), field);
            final Paint color = this.picker.getColor();
            rectangle.setStroke(color);
            rectangle.setFill(Color.TRANSPARENT);
            result.add(new Box(field.getPage(), field.getType(), rectangle, color));
        }
        return result;
    }

    private Rectangle getRectangle(final Size size, final FieldDTO f) {
        final double xPos = (f.getxPosPercentage() * size.width()) + AnchorPane.getLeftAnchor(imageView);
        final double yPos = (f.getyPosPercentage() * size.height()) + AnchorPane.getTopAnchor(imageView);
        final double width = f.getWidthPercentage() * size.width();
        final double height = f.getHeightPercentage() * size.height();
        return new Rectangle(xPos, yPos, width, height);
    }

    private Size getSize(final ImageView imageView) {
        final Bounds boundsInParent = imageView.getBoundsInParent();
        return new Size(boundsInParent.getHeight(), boundsInParent.getWidth());
    }
}
