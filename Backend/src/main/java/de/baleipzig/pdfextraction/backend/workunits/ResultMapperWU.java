package de.baleipzig.pdfextraction.backend.workunits;

import de.baleipzig.pdfextraction.api.fields.FieldType;
import de.baleipzig.pdfextraction.backend.entities.Field;
import de.baleipzig.pdfextraction.backend.util.FieldUtils;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultMapperWU {
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String DATE_FIELD = "DATE";
    private static final String SENDER_NAME_FIELD = "SENDER_NAME";
    private static final String ADDRESS_SENDER = FieldType.ADDRESS_SENDER.name();
    private static final String ADDRESS_RECEIVER = FieldType.ADDRESS_RECEIVER.name();

    //Whoever touches this is responsible
    private static final Pattern namePattern = Pattern.compile("^((?:[ .-]?\\w+[ .-]?)+)?\\s+((?:[ ]?\\w+[ ]?)+)\\s+((?:[ .-]?\\w+[ .-]?)+\\d+\\w*)\\s+(\\d+(?:[ ]\\w+[ ]?)*)$");

    private final Map<Field, String> extracted;

    public ResultMapperWU(Map<Field, String> extracted) {
        this.extracted = extracted;
    }

    public Map<String, String> work() {
        checkResult();

        final Map<String, String> result = FieldUtils.map(this.extracted);
        result.put(DATE_FIELD, DAY_FORMAT.format(LocalDateTime.now()));
        result.put(SENDER_NAME_FIELD, parseSenderName(result.get(ADDRESS_RECEIVER)));

        //Now the original sender becomes the receiver and vice versa
        final String sender = result.get(ADDRESS_SENDER);
        result.put(ADDRESS_SENDER, convertReceiverToSender(result.get(ADDRESS_RECEIVER)));
        result.put(ADDRESS_RECEIVER, convertSenderToReceiver(sender));

        LoggerFactory.getLogger(getClass())
                .trace("Mapped: {}", result);
        return result;
    }

    private void checkResult() {
        final List<String> keyWithNoValue = this.extracted.entrySet()
                .stream()
                .filter(e -> !StringUtils.hasText(e.getValue()))
                .map(Map.Entry::getKey)
                .map(Field::getType)
                .map(FieldType::name)
                .toList();

        if (!keyWithNoValue.isEmpty()) {
            LoggerFactory.getLogger(getClass())
                    .error("Could not find extract any Text for Fields: {}", keyWithNoValue);
            throw new IllegalArgumentException("Empty Key's " + keyWithNoValue);
        }
    }

    private String parseSenderName(String senderAddress) {
        final Matcher matcher = namePattern.matcher(senderAddress);
        if (matcher.matches()) {
            return matcher.group(2);
        }

        LoggerFactory.getLogger(getClass())
                .warn("Could not match address to \"{}\"", senderAddress);

        //fallback
        return Arrays.stream(senderAddress.split("\\n"))
                .filter(StringUtils::hasText)
                .skip(1)
                .findFirst()
                .orElseThrow();
    }

    private String convertSenderToReceiver(String sender) {

        return sender.replace(", ", "\n");
    }

    private String convertReceiverToSender(String receiver) {
        StringJoiner joiner = new StringJoiner(", ");
        final String[] split = receiver.split("\\n");
        if (split.length < 2) {
            throw new IllegalArgumentException("Receiver has no line-breaks \"" + receiver + "\"");
        }


        //Skipping 1. line as it's usually just Mr/Mrs/Dr....
        for (int i = 1; i < split.length; i++) {
            final String s = split[i];
            if (StringUtils.hasText(s)) {
                joiner.add(s);
            }
        }
        return split[0] + " " + joiner;
    }
}
