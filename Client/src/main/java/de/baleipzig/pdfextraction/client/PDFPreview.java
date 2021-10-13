package de.baleipzig.pdfextraction.client;

import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;


public class PDFPreview {

    private static final PDFPreview instance = new PDFPreview();
    private Path pdfPath;
    private int numberOfPages = 0;
    // current page zählt von 1 aus, startet also nicht bei 0
    private int currentPage = 1;
    private PDFPreview() {
    }

    public static PDFPreview getInstance() {
        return instance;
    }

    public boolean hasNextPage() {
        return hasPreview() && this.currentPage < this.numberOfPages;
    }

    public boolean hasPreviousPage() {
        return hasPreview() && this.currentPage > 0;
    }

    public boolean hasPreview() {
        return this.pdfPath != null;
    }

    public Image getCurrentPreview() {
        if (!hasPreview()) {
            throw new IllegalStateException("No current Page available.");
        }

        return createPreviewImage();
    }

    public Image getNextPreview() {
        if (!hasNextPage()) {
            throw new IllegalStateException("No next page available.");
        }

        this.currentPage++;
        return createPreviewImage();
    }

    public Image getPreviousPreview() {
        if (!hasPreviousPage()) {
            throw new IllegalStateException("No previous page available");
        }

        this.currentPage--;
        return createPreviewImage();
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setPdfPath(Path pdfPath) {
        this.pdfPath = pdfPath;
        this.currentPage = 0;

        refreshPageNumber();
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    private void refreshPageNumber() {
        try (final InputStream input = Files.newInputStream(this.pdfPath);
             final PDDocument document = PDDocument.load(input)) {
            this.numberOfPages = document.getNumberOfPages();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Image createPreviewImage() {
        try {
            BufferedImage bufferedPDFImage = convertPDFtoImage();
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedPDFImage, "PNG", outputStream);

            return new Image(new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private BufferedImage convertPDFtoImage()
            throws IOException {
        try (InputStream input = Files.newInputStream(this.pdfPath);
             PDDocument document = PDDocument.load(input)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            return pdfRenderer.renderImage(this.currentPage);
        }
    }
}
