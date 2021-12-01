package de.baleipzig.pdfextraction.backend.workunits;

import de.baleipzig.pdfextraction.backend.entities.Field;
import de.baleipzig.pdfextraction.backend.tesseract.Tess;
import fr.opensagres.xdocreport.core.XDocReportException;
import net.sourceforge.tess4j.TesseractException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CompleteExtractionWU {
    private final List<Field> fields;
    private final byte[] source;
    private final byte[] dest;

    public CompleteExtractionWU(List<Field> fields, byte[] source, byte[] dest) {
        this.fields = fields;
        this.source = source;
        this.dest = dest;
    }

    public byte[] work(final Tess tess)
            throws TesseractException, IOException, XDocReportException {
        final Map<Field, String> extracted = new CombinedExtractedWU(this.fields, this.source)
                .work(tess);

        final Map<String, String> result = new ResultMapperWU(extracted).work();
        return new XDocWU(result, this.dest).work();
    }
}
