package de.baleipzig.pdfextraction.client.workunits;

import de.baleipzig.pdfextraction.api.dto.FieldDTO;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.client.utils.Box;
import de.baleipzig.pdfextraction.client.utils.Size;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DrawRectangleWU {

    private final ImageView imageView;
    private final TemplateDTO templateDTO;
    private final int currentPage;

    

    public DrawRectangleWU(ImageView imageView, TemplateDTO templateDTO, int currentPage) {
        this.imageView = imageView;
        this.templateDTO = templateDTO;
        this.currentPage = currentPage;
    }

    /**
     * Creates rectangles out of the in the constructor loaded TemplateDTO, that can be added on an container
     * @return a List of javafx Rectangles
     */
    public Set<Box> work() {
        Set<Box> drawnBoxes = new HashSet<>();

        List<FieldDTO> boxes = templateDTO.getFields();
        List<Rectangle> rectangles = new ArrayList<>();
        for (FieldDTO field : boxes) {
            if (field.getPage() == currentPage) {
                Rectangle rectangle = getRectangle(getSize(imageView), field);
                Paint color = new ColorPicker(drawnBoxes).getColor();
                rectangle.setStroke(color);
                rectangle.setFill(Color.TRANSPARENT);
                rectangles.add(rectangle);
                drawnBoxes.add(new Box(field.getPage(), field.getType(), rectangle, color));
            }
        }
        return drawnBoxes;
    }

    private Rectangle getRectangle(Size size, FieldDTO f) {
        final double xPos = (f.getxPosPercentage() * size.width()) + AnchorPane.getLeftAnchor(imageView);
        final double yPos = (f.getyPosPercentage() * size.height()) + AnchorPane.getTopAnchor(imageView);
        final double width = f.getWidthPercentage() * size.width();
        final double height = f.getHeightPercentage() * size.height();
        return new Rectangle(xPos, yPos, width, height);
    }

    private Size getSize(final ImageView imageView) {
        Bounds boundsInParent = imageView.getBoundsInParent();
        return new Size(boundsInParent.getHeight(), boundsInParent.getWidth());
    }


}
