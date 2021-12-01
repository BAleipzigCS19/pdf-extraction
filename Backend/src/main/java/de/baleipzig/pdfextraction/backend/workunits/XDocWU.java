package de.baleipzig.pdfextraction.backend.workunits;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.core.document.DocumentKind;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class XDocWU {
    private final byte[] resultContent;
    private final Map<String, String> result;

    public XDocWU(final Map<String, String> result,
                  final byte[] resultContent) {
        this.resultContent = resultContent;
        this.result = result;
    }

    /**
     * Inlines the Fields defined in the result-Template and converts the resulting File to PDF
     *
     * @return Bytes of the resulting PDF-File
     * @throws IOException         The given Content was not a valid File
     * @throws XDocReportException Something went wrong when converting to PDF or could not load the initial result template
     */
    public byte[] work()
            throws IOException, XDocReportException {
        final IXDocReport report = XDocReportRegistry.getRegistry().loadReport(new ByteArrayInputStream(this.resultContent), TemplateEngineKind.Velocity);
        final IContext context = report.createContext();
        this.result.forEach(context::put);

        final Options options = Options.getFrom(DocumentKind.ODT).to(ConverterTypeTo.PDF);
        final ByteArrayOutputStream processed = new ByteArrayOutputStream(this.resultContent.length);
        report.convert(context, options, processed);

        return processed.toByteArray();
    }

}
