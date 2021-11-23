package de.baleipzig.pdfextraction.backend.workunits;

import de.baleipzig.pdfextraction.api.fields.FieldType;
import de.baleipzig.pdfextraction.backend.entities.Field;
import de.baleipzig.pdfextraction.backend.util.FieldUtils;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.core.document.DocumentKind;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class XDocWU {
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String DATE_FIELD = "DATE";
    private static final String SENDER_NAME_FIELD = "SENDER_NAME";
    private static final String ADDRESS_SENDER = "ADDRESS_SENDER";
    private static final String ADDRESS_RECEIVER = "ADDRESS_RECEIVER";

    private final byte[] resultContent;
    private final Map<Field, String> extracted;

    public XDocWU(final Map<Field, String> extracted,
                  final byte[] resultContent) {
        this.resultContent = resultContent;
        this.extracted = extracted;
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
        checkResult(extracted);

        final Map<String, String> result = FieldUtils.map(extracted);
        result.put(DATE_FIELD, DAY_FORMAT.format(LocalDateTime.now()));
        result.put(SENDER_NAME_FIELD, parseSenderName(result.get(ADDRESS_RECEIVER)));
        result.put(ADDRESS_SENDER, convertSenderToReceiver(result.get(ADDRESS_SENDER)));
        result.put(ADDRESS_RECEIVER, convertSenderToReceiver(result.get(ADDRESS_RECEIVER)));

        LoggerFactory.getLogger(getClass())
                .trace("Extracted: {}", result);

        return inlineResult(result);
    }

    private void checkResult(final Map<Field, String> result) {
        final List<String> keyWithNoValue = result.entrySet()
                .stream()
                .filter(e -> !StringUtils.hasText(e.getValue()))
                .map(Map.Entry::getKey)
                .map(Field::getType)
                .map(FieldType::name)
                .toList();

        if (!keyWithNoValue.isEmpty()) {
            LoggerFactory.getLogger(getClass())
                    .warn("Could not find extract any Text for Fields: {}", keyWithNoValue);
        }
    }

    private byte[] inlineResult(final Map<String, String> extracted)
            throws IOException, XDocReportException {
        final IXDocReport report = XDocReportRegistry.getRegistry().loadReport(new ByteArrayInputStream(this.resultContent), TemplateEngineKind.Velocity);
        final IContext context = report.createContext();
        extracted.forEach(context::put);

        final Options options = Options.getFrom(DocumentKind.ODT).to(ConverterTypeTo.PDF);
        final ByteArrayOutputStream processed = new ByteArrayOutputStream(this.resultContent.length);
        report.convert(context, options, processed);

        return processed.toByteArray();
    }

    private String parseSenderName(String senderAddress){

        return senderAddress.split("\n")[1];
    }

    private String convertSenderToReceiver(String sender){

        return sender.replace(", ", "\n");
    }

    private String convertReceiverToSender(String receiver){

        return receiver.replace("\n", ", ");
    }
}
