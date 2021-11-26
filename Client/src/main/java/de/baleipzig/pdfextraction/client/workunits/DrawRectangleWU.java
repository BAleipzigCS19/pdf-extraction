package de.baleipzig.pdfextraction.client.workunits;

import de.baleipzig.pdfextraction.api.dto.FieldDTO;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
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

public record DrawRectangleWU(ImageView imageView, TemplateDTO templateDTO) {

    /**
     * Creates rectangles out of the in the constructor loaded TemplateDTO, that can be added on a container
     *
     * @return a List of javafx Rectangles
     */
    public Set<Box> work() {
        Set<Box> templateBoxes = new HashSet<>();
        final ColorPicker picker = new ColorPicker(templateBoxes);

        List<FieldDTO> boxes = templateDTO.getFields();
        for (FieldDTO field : boxes) {
                final Rectangle rectangle = getRectangle(getSize(imageView), field);
                final Paint color = picker.getColor();
                rectangle.setStroke(color);
                rectangle.setFill(Color.TRANSPARENT);
                templateBoxes.add(new Box(field.getPage(), field.getType(), rectangle, color));
        }
        return templateBoxes;
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
