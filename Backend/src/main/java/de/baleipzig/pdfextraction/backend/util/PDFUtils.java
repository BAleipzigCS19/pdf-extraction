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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PDFUtils {

    /**
     * Converts the first page to an Image to check the Box-placement
     *
     * @param template    Template which provides the fields
     * @param fileContent Content-Array of the File
     * @return An Image of the first page with the Fields marked
     */
    public static RenderedImage toImage(final Template template, final byte[] fileContent) {
        try (final PDDocument document = PDDocument.load(fileContent)) {
            final PDFRenderer renderer = new PDFRenderer(document);
            final BufferedImage image = renderer.renderImage(0);
            final int totalHeight = image.getHeight();
            final int totalWidth = image.getWidth();

            final Graphics g = image.createGraphics();
            g.setColor(Color.RED);
            for (Field f : template.getFields()) {
                if (f.getPage() == 0) {
                    final int xPos = (int) (f.getxPosPercentage() * totalWidth);
                    final int yPos = (int) (f.getyPosPercentage() * totalHeight);
                    final int width = (int) (f.getWidthPercentage() * totalWidth);
                    final int height = (int) (f.getHeightPercentage() * totalHeight);

                    g.drawRect(xPos, yPos, width, height);
                    g.drawString(f.getType().getName(), xPos + 5, yPos + 10);
                }
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
     * @param template    Template with the Fielddefinition
     * @param fileContent Content of the PDF-File
     * @return Map of Fields and their resolved Text
     */
    public static Map<Field, String> extract(final Template template, final byte[] fileContent) {
        final Map<Field, String> result = new HashMap<>(template.getFields().size());

        try (final PDDocument document = PDDocument.load(fileContent)) {
            final PDFRenderer renderer = new PDFRenderer(document);

            final Map<Integer, List<Field>> map = template.getFields()
                    .stream()
                    .collect(Collectors.groupingBy(Field::getPage));

            for (Map.Entry<Integer, List<Field>> entry : map.entrySet()) {
                final PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                final BufferedImage image = renderer.renderImage(entry.getKey());
                final int totalHeight = image.getHeight();
                final int totalWidth = image.getWidth();

                for (final Field f : entry.getValue()) {
                    final double xPos = f.getxPosPercentage() * totalWidth;
                    final double yPos = f.getyPosPercentage() * totalHeight;
                    final double width = f.getWidthPercentage() * totalWidth;
                    final double height = f.getHeightPercentage() * totalHeight;

                    stripper.addRegion(f.getType().name(), new Rectangle2D.Double(xPos, yPos, width, height));
                }

                stripper.extractRegions(document.getPage(entry.getKey()));
                entry.getValue().forEach(f -> result.put(f, stripper.getTextForRegion(f.getType().name())));
            }

            return result;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private PDFUtils() {
    }
}
