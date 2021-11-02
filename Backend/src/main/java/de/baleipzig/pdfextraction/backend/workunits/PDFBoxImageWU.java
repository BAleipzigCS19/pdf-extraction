package de.baleipzig.pdfextraction.backend.workunits;

import de.baleipzig.pdfextraction.backend.entities.Field;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

public class PDFBoxImageWU extends AbstractPDFBoxWU {

    private final int pageToRender;

    public PDFBoxImageWU(final Collection<Field> fields,
                         final byte[] sourcePDF) {
        this(fields, sourcePDF, 0);
    }

    public PDFBoxImageWU(final Collection<Field> fields,
                         final byte[] sourcePDF,
                         final int pageToRender) {
        super(fields, sourcePDF);
        this.pageToRender = pageToRender;
    }

    /**
     * Renders the requested Page (default: Page 0) with the fields to an image.
     *
     * @return PNG-Bytes of the resulting image
     * @throws IOException The given Content was not a valid PDF-File
     */
    public byte[] work()
            throws IOException {
        return convertToBytes(renderPage());
    }

    private byte[] convertToBytes(BufferedImage image)
            throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", stream);
        return stream.toByteArray();
    }

    private BufferedImage renderPage()
            throws IOException {

        try (final PDDocument document = PDDocument.load(this.sourcePDF)) {
            final PDFRenderer renderer = new PDFRenderer(document);
            final BufferedImage image = renderer.renderImage(this.pageToRender);
            final Size size = getSize(image);

            final Graphics g = image.createGraphics();
            g.setColor(Color.RED);
            for (Field field : this.fields) {
                if (field.getPage() != pageToRender) {
                    continue;
                }

                final Rectangle2D rec = getRectangle(size, field);

                g.drawRect((int) rec.getX(), (int) rec.getY(), (int) rec.getWidth(), (int) rec.getHeight());
                g.drawString(field.getType().getName(), (int) rec.getX() + 5, (int) rec.getY() - 10);
            }
            g.dispose();
            return image;
        }
    }
}
