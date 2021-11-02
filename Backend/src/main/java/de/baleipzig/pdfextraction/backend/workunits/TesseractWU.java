package de.baleipzig.pdfextraction.backend.workunits;

import de.baleipzig.pdfextraction.backend.entities.Field;
import de.baleipzig.pdfextraction.backend.tesseract.Tess;
import net.sourceforge.tess4j.TesseractException;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class TesseractWU {
    private final Collection<Field> fields;
    private final byte[] sourcePDF;


    public TesseractWU(final Collection<Field> fields, final byte[] sourcePDF) {
        this.fields = fields;
        this.sourcePDF = sourcePDF;
    }


    /**
     * Extracts the given Fields of the given PDF-File with tesseract.
     *
     * @param tess Access to an installed tesseract
     * @return Map of Fields with their extracted Result
     * @throws IOException        The given Contetnt was not a valid PDF-File
     * @throws TesseractException Severe misconfiguration with tesseract
     */
    public Map<Field, String> work(final Tess tess)
            throws IOException, TesseractException {

        return tess.doBatchOCR(this.fields, this.sourcePDF);
    }
}
