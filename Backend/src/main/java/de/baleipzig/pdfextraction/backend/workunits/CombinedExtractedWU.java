package de.baleipzig.pdfextraction.backend.workunits;

import de.baleipzig.pdfextraction.api.fields.FieldType;
import de.baleipzig.pdfextraction.backend.entities.Field;
import de.baleipzig.pdfextraction.backend.tesseract.Tess;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CombinedExtractedWU {
    private final Collection<Field> fields;
    private final byte[] sourcePDF;

    public CombinedExtractedWU(Collection<Field> fields, byte[] sourcePDF) {
        this.fields = fields;
        this.sourcePDF = sourcePDF;
    }

    /**
     * Combines the Extraction of {@link PDFBoxExtractionWU} with {@link TesseractWU}.
     * <p>
     * The Extraction with PDFBox has precedence and tesseract is the backup.
     *
     * @param tess Access to an installed tesseract
     * @return Returns the Fields and their extracted Value.
     * @throws IOException        The given Content was not a Valid PDF-File
     * @throws TesseractException Some severe misconfiguration with tesseract
     */
    public Map<Field, String> work(final Tess tess)
            throws IOException, TesseractException {
        final Map<Field, String> extracted = new PDFBoxExtractionWU(this.fields, this.sourcePDF).work();

        final Set<Field> emptyFields = getEmptyFields(extracted);

        if (!emptyFields.isEmpty()) {
            LoggerFactory.getLogger(getClass())
                    .debug("Could not extract Text with PDFBox for fields {}", emptyFields.stream().map(Field::getType).map(FieldType::getName).toList());

            extracted.putAll(new TesseractWU(emptyFields, this.sourcePDF).work(tess));
        }

        return extracted;
    }

    private Set<Field> getEmptyFields(Map<Field, String> extracted) {
        return extracted.entrySet()
                .stream()
                .filter(entry -> !StringUtils.hasText(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
