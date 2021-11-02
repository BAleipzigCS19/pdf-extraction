package de.baleipzig.pdfextraction.backend.workunits;

import de.baleipzig.pdfextraction.backend.entities.Field;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collection;

abstract class AbstractPDFBoxWU {
    protected final Collection<Field> fields;
    protected final byte[] sourcePDF;


    protected AbstractPDFBoxWU(Collection<Field> fields, byte[] sourcePDF) {
        this.fields = fields;
        this.sourcePDF = sourcePDF;
    }


    protected Rectangle2D getRectangle(Size size, Field f) {
        final double xPos = f.getxPosPercentage() * size.width;
        final double yPos = f.getyPosPercentage() * size.height;
        final double width = f.getWidthPercentage() * size.width;
        final double height = f.getHeightPercentage() * size.height;
        return new Rectangle2D.Double(xPos, yPos, width, height);
    }

    protected Size getSize(final BufferedImage image) {
        return new Size(image.getHeight(), image.getWidth());
    }

    protected record Size(int height, int width) {
    }
}
