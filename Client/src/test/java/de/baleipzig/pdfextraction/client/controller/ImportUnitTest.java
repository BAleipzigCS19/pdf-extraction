package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.api.dto.FieldDTO;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.api.fields.FieldType;
import de.baleipzig.pdfextraction.client.utils.Box;
import de.baleipzig.pdfextraction.client.workunits.DrawRectangleWU;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ImportUnitTest {


    @Test
    void createRectanglesTest() {

        final int WIDTH = 800;
        final int HEIGHT = 1000;

        final double leftMargin = 5d;
        final double topMargin = 10d;
        final double percXPos = 0.1;
        final double percYPos = 0.2;
        final double percWidth = 0.3;
        final double percHeight = 0.4;

        ImageView imageView = new ImageView();
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);
        AnchorPane.setLeftAnchor(imageView, leftMargin);
        AnchorPane.setTopAnchor(imageView, topMargin);

        List<FieldDTO> list = List.of(new FieldDTO(FieldType.ADDRESS_RECEIVER, 0, percXPos, percYPos, percWidth, percHeight));
        TemplateDTO templateDTO = new TemplateDTO("Test", "Test", list);

        DrawRectangleWU drawRectangleWU = new DrawRectangleWU(imageView, templateDTO, 0);
        Set<Box> boxes = drawRectangleWU.work();
        List<Rectangle> rectangles = new ArrayList<>();
        boxes.stream().toList().forEach(e -> rectangles.add(e.place()));

        assertNotNull(rectangles);
        assertEquals(1, rectangles.size());

        Rectangle rectangle = rectangles.get(0);
        assertEquals(WIDTH * percWidth, rectangle.getWidth());
        assertEquals(HEIGHT * percHeight, rectangle.getHeight());
        assertEquals(percXPos * WIDTH + leftMargin, rectangle.getX());
        assertEquals(percYPos * HEIGHT + topMargin, rectangle.getY());
    }
}
