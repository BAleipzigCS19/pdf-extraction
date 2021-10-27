package de.baleipzig.pdfextraction.backend.tesseract;

import de.baleipzig.pdfextraction.backend.config.Config;
import de.baleipzig.pdfextraction.backend.entities.Field;
import de.baleipzig.pdfextraction.backend.entities.Template;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class TessImpl implements Tess {

    private final ITesseract tesseract;

    public TessImpl(final Config config) {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath(config.getTessPath());
        this.tesseract.setLanguage(config.getTessLanguage());
        this.tesseract.setTessVariable("user_defined_dpi", "300");
    }

    private static Rectangle getSize(final Field field, final BufferedImage image) {
        final int totalHeight = image.getHeight();
        final int totalWidth = image.getWidth();

        final int xPos = (int) (field.getxPosPercentage() * totalWidth);
        final int yPos = (int) (field.getyPosPercentage() * totalHeight);
        final int width = (int) (field.getWidthPercentage() * totalWidth);
        final int height = (int) (field.getHeightPercentage() * totalHeight);
        return new Rectangle(xPos, yPos, width, height);
    }

    @Override
    public Map<Field, String> doOCR(final Template template, final byte[] fileContent) {
        final Map<Field, String> result = new HashMap<>(template.getFields().size());

        try (final PDDocument document = PDDocument.load(fileContent)) {
            final PDFRenderer renderer = new PDFRenderer(document);

            for (final Field field : template.getFields()) {
                final BufferedImage image = renderer.renderImageWithDPI(field.getPage(), 300, ImageType.GRAY);
                result.put(field, this.tesseract.doOCR(image, getSize(field, image)));
            }

            return result;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (TesseractException e) {
            throw new UncheckedIOException(new IOException(e));
        }
    }
}
