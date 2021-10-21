package de.baleipzig.pdfextraction.client.utils;

import jakarta.inject.Singleton;
import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Singleton
public class PDFRenderer {

    private RendererWrapper wrapper;
    private int numberOfPages = 0;
    private int currentPage = 0;

    private PDFRenderer() {
    }

    /**
     * Checks if a Page after the current is available.
     *
     * @return If such a page is available {@code true}, else {@code false}
     */
    public boolean hasNextPage() {
        return hasPreview() && this.currentPage + 1 < this.numberOfPages;
    }


    /**
     * Checks if a Page before the current is available.
     *
     * @return If such a page is available {@code true}, else {@code false}
     */
    public boolean hasPreviousPage() {
        return hasPreview() && this.currentPage > 0;
    }


    /**
     * Checks if a Page is available.
     *
     * @return If a page is available {@code true}, else {@code false}
     */
    public boolean hasPreview() {
        return this.wrapper != null;
    }

    /**
     * Returns the current Page as Image if it exists.
     *
     * @return The Image of the current Page
     */
    public Image getCurrentPreview() {
        if (!hasPreview()) {
            throw new IllegalStateException("No current Page available.");
        }

        return createPreviewImage();
    }

    /**
     * Returns the next Page as Image if it exists.
     *
     * @return The Image of the next Page
     */
    public Image getNextPreview() {
        if (!hasNextPage()) {
            throw new IllegalStateException("No next page available.");
        }

        this.currentPage++;
        return createPreviewImage();
    }

    /**
     * Returns the previous Page as Image if it exists.
     *
     * @return The Image of the previous Page
     */
    public Image getPreviousPreview() {
        if (!hasPreviousPage()) {
            throw new IllegalStateException("No previous page available");
        }

        this.currentPage--;
        return createPreviewImage();
    }

    /**
     * Returns the 0-based Index of the current page
     *
     * @return 0-based current index
     */
    public int getCurrentPage() {
        return this.currentPage;
    }

    /**
     * Sets a new index
     *
     * @param currentPage The 0-based index
     */
    public void setCurrentPage(int currentPage) {
        if (currentPage < 0 || currentPage >= numberOfPages) {
            throw new IllegalArgumentException("New page index \"%d\" out of Range of \"%d\"".formatted(currentPage, this.numberOfPages));
        }

        this.currentPage = currentPage;
    }

    /**
     * Sets the Path to the PDF to display
     *
     * @param pdfPath Path to an PDF File, where the Preview is generated from.
     */
    public void setPdfPath(Path pdfPath) {
        this.currentPage = 0;

        Optional.ofNullable(this.wrapper)
                .ifPresent(RendererWrapper::close);
        this.wrapper = newRenderer(pdfPath);
        refreshPageNumber(pdfPath);
    }

    /**
     * Returns the amount on Pages the current PDF has
     *
     * @return Number of Pages of the current PDF
     */
    public int getNumberOfPages() {
        if (!this.hasPreview()) {
            throw new IllegalStateException("No PDF file loaded.");
        }

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
            return new RendererWrapper(new org.apache.pdfbox.rendering.PDFRenderer(document), document);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private record RendererWrapper(org.apache.pdfbox.rendering.PDFRenderer renderer,
                                   PDDocument document) implements Closeable {
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
