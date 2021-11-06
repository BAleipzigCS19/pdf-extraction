package de.baleipzig.pdfextraction.backend.workunits;

import de.baleipzig.pdfextraction.backend.entities.Field;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PDFBoxExtractionWU extends AbstractPDFBoxWU {

    public PDFBoxExtractionWU(final Collection<Field> fields,
                              final byte[] sourcePDF) {
        super(fields, sourcePDF);
    }

    /**
     * Extracts the fields with the given PDF-File
     *
     * @return Map of Fields and their extracted Field.
     * @throws IOException The given Content was not a valid PDF-File
     */
    public Map<Field, String> work()
            throws IOException {
        final Map<Field, String> result = new HashMap<>(fields.size());

        try (final PDDocument document = PDDocument.load(this.sourcePDF)) {
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
                entry.getValue().forEach(f -> result.put(f, stripper.getTextForRegion(f.getType().name()).trim()));
            }

            return result;
        }
    }
}
