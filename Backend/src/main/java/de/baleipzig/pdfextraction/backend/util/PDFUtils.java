package de.baleipzig.pdfextraction.backend.util;

import de.baleipzig.pdfextraction.backend.entities.Field;
import de.baleipzig.pdfextraction.backend.entities.Template;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class PDFUtils {

    /**
     * Converts the first page to an Image to check the Box-placement
     *
     * @param template    Template which provides the fields
     * @param fileContent Content-Array of the File
     * @return An Image of the first page with the Fields marked
     */
    public static RenderedImage toImage(final Template template, final byte[] fileContent) {
        final int pageToRender = 0;

        try (final PDDocument document = PDDocument.load(fileContent)) {
            final PDFRenderer renderer = new PDFRenderer(document);
            final BufferedImage image = renderer.renderImage(pageToRender);
            final Size size = getSize(image);

            final Graphics g = image.createGraphics();
            g.setColor(Color.RED);
            for (Field field : template.getFields()) {
                if (field.getPage() != pageToRender) {
                    continue;
                }

                final Rectangle2D rec = getRectangle(size, field);

                g.drawRect((int) rec.getX(), (int) rec.getY(), (int) rec.getWidth(), (int) rec.getHeight());
                g.drawString(field.getType().getName(), (int) rec.getX() + 5, (int) rec.getY() + 10);
            }
            g.dispose();
            return image;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Attempts to extract the information form the given PDF with the template
     *
     * @param fields      Collection of Fields to Extract
     * @param fileContent Content of the PDF-File
     * @return Map of Fields and their resolved Text
     */
    public static Map<Field, String> extract(final Collection<Field> fields, final byte[] fileContent) {
        final Map<Field, String> result = new HashMap<>(fields.size());

        try (final PDDocument document = PDDocument.load(fileContent)) {
            final PDFRenderer renderer = new PDFRenderer(document);

            final Map<Integer, List<Field>> map = fields
                    .stream()
                    .collect(Collectors.groupingBy(Field::getPage));

            for (Map.Entry<Integer, List<Field>> entry : map.entrySet()) {
                final PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                final BufferedImage image = renderer.renderImage(entry.getKey());
                final Size size = getSize(image);

                for (final Field field : entry.getValue()) {
                    final Rectangle2D rect = getRectangle(size, field);
                    stripper.addRegion(field.getType().name(), rect);
                }

                stripper.extractRegions(document.getPage(entry.getKey()));
                entry.getValue().forEach(f -> result.put(f, stripper.getTextForRegion(f.getType().name())));
            }

            return result;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Rectangle2D getRectangle(Size size, Field f) {
        final double xPos = f.getxPosPercentage() * size.width;
        final double yPos = f.getyPosPercentage() * size.height;
        final double width = f.getWidthPercentage() * size.width;
        final double height = f.getHeightPercentage() * size.height;
        return new Rectangle2D.Double(xPos, yPos, width, height);
    }

    private static Size getSize(final BufferedImage image) {
        return new Size(image.getHeight(), image.getWidth());
    }

    private record Size(int height, int width) {
    }

    private PDFUtils() {
    }
}
