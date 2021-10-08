package de.baleipzig.pdfextraction.client;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;


public class PDFPreview {

    public static int numberOfPages = 0;

    public static int getCurrentPage() {
        return currentPage;
    }

    public static void setCurrentPage(int currentPage) {
        PDFPreview.currentPage = currentPage;
    }

    // current page zählt von 1 aus, startet also nicht bei 0
    public static int currentPage = 1;

    public static Image createPreviewImage(final int pageIndex, final Path pdfPath) {

        try {

            BufferedImage bufferedPDFImage = PDFPreview.convertPDFtoImage(pdfPath, pageIndex);
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedPDFImage, "PNG", outputStream);

            return new Image(new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (IOException e) {
            LoggerFactory.getLogger(PDFPreview.class)
                    .atError()
                    .addArgument(pdfPath)
                    .setCause(e)
                    .log("Exception occurred while converting the pdf {} into an image");
        }

        return null;
    }

    private static BufferedImage convertPDFtoImage(final Path pdfPath, final int pageIndex) throws IOException {

        try (InputStream input = Files.newInputStream(pdfPath);
             PDDocument document = PDDocument.load(input)) {

            numberOfPages = document.getNumberOfPages();
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            return pdfRenderer.renderImage(pageIndex);
        }

    }

}
