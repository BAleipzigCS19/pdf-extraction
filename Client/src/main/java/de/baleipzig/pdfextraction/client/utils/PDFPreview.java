package de.baleipzig.pdfextraction.client.utils;

import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;


public class PDFPreview {

    private static final PDFPreview instance = new PDFPreview();

    private RendererWrapper wrapper;
    private int numberOfPages = 0;
    private int currentPage = 0;

    private PDFPreview() {
    }

    public static PDFPreview getInstance() {
        return instance;
    }

    public boolean hasNextPage() {
        return hasPreview() && this.currentPage + 1 < this.numberOfPages;
    }

    public boolean hasPreviousPage() {
        return hasPreview() && this.currentPage > 0;
    }

    public boolean hasPreview() {
        return this.wrapper != null;
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
        this.currentPage = 0;

        Optional.ofNullable(this.wrapper)
                .ifPresent(RendererWrapper::close);
        this.wrapper = newRenderer(pdfPath);
        refreshPageNumber(pdfPath);
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    private void refreshPageNumber(final Path path) {
        try (final InputStream input = Files.newInputStream(path);
             final PDDocument document = PDDocument.load(input)) {
            this.numberOfPages = document.getNumberOfPages();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Image createPreviewImage() {
        try {
            BufferedImage bufferedPDFImage = this.wrapper.renderer.renderImage(this.currentPage);
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedPDFImage, "PNG", outputStream);

            return new Image(new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private RendererWrapper newRenderer(final Path path) {
        try (InputStream input = Files.newInputStream(path)) {
            final PDDocument document = PDDocument.load(input);
            return new RendererWrapper(new PDFRenderer(document), document);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private record RendererWrapper(PDFRenderer renderer, PDDocument document) implements Closeable {
        @Override
        public void close() {
            try {
                this.document.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
