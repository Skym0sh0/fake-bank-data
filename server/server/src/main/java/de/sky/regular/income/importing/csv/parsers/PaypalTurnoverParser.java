package de.sky.regular.income.importing.csv.parsers;

import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import de.sky.regular.income.api.turnovers.TurnoverImportFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class PaypalTurnoverParser implements TurnoverParser {
    @Override
    public TurnoverImportFormat getSupportedFormat() {
        return TurnoverImportFormat.PAYPAL;
    }

    @Override
    public List<TurnoverRecord> parseCsv(Reader reader) throws Exception {
        log.info("Preparing CSV parser...");
        var proc = new BeanListProcessor<>(PaypalRecord.class, 1000);

        var settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(true);
        settings.setProcessor(proc);
        settings.setDelimiterDetectionEnabled(true);
        settings.setLineSeparatorDetectionEnabled(true);

        var parser = new CsvParser(settings);

        log.info("Parsing CSV...");

        parser.parse(reader);

        log.info("CSV parsed successfully");

        var result = proc.getBeans();

        log.info("Found {} records", result.size());

        return result.stream()
                .map(PaypalRecord::toTurnOverRecord)
                .toList();
    }

    @Data
    public static class PaypalRecord {
        @Parsed(field = "Datum")
        @Convert(conversionClass = CsvProcessorConverters.DateConverter.class)
        private LocalDate date;
        @Parsed(field = "Uhrzeit")
        @Convert(conversionClass = CsvProcessorConverters.TimeConverter.class)
        private LocalTime time;
        @Parsed(field = "Zeitzone")
        @Convert(conversionClass = CsvProcessorConverters.TimeZoneConverter.class)
        private java.time.ZoneId timezone;
        @Parsed(field = "Beschreibung")
        private String description;
        @Parsed(field = "Währung")
        private String currency;
        @Parsed(field = "Brutto")
        @Convert(conversionClass = CsvProcessorConverters.MoneyAmountConverter.class)
        private int bruttoAmountInCents;
        @Parsed(field = "Entgelt")
        @Convert(conversionClass = CsvProcessorConverters.MoneyAmountConverter.class)
        private int costsAmountInCents;
        @Parsed(field = "Netto")
        @Convert(conversionClass = CsvProcessorConverters.MoneyAmountConverter.class)
        private int nettoAmountInCents;
        @Parsed(field = "Guthaben")
        @Convert(conversionClass = CsvProcessorConverters.MoneyAmountConverter.class)
        private int balanceAmountInCents;
        @Parsed(field = "Transaktionscode")
        private String transactionCode;
        @Parsed(field = "Absender E-Mail-Adresse")
        private String senderEmail;
        @Parsed(field = "Name")
        private String senderName;
        @Parsed(field = "Name der Bank")
        private String bankName;
        @Parsed(field = "Bankkonto")
        private String bankAccountEnding;
        @Parsed(field = "Versand- und Bearbeitungsgebühr")
        @Convert(conversionClass = CsvProcessorConverters.MoneyAmountConverter.class)
        private int processingCostAmountInCents;
        @Parsed(field = "Umsatzsteuer")
        @Convert(conversionClass = CsvProcessorConverters.MoneyAmountConverter.class)
        private int taxAmountInCents;
        @Parsed(field = "Rechnungsnummer")
        private String invoiceNumber;
        @Parsed(field = "Zugehöriger Transaktionscode")
        private String associatedTransactionCode;

        public ZonedDateTime getTimestamp() {
            return ZonedDateTime.of(getDate(), getTime(), getTimezone());
        }

        public String getCombinedDescription() {
            return Stream.of(
                            Optional.ofNullable(getDescription()).map(e -> "Beschreibung: " + e).orElse(null),
                            Optional.ofNullable(getInvoiceNumber()).map(e -> "Rechnungsnummer: " + e).orElse(null),
                            Optional.ofNullable(getTransactionCode()).map(e -> "Transaktionscode: " + e).orElse(null),
                            Optional.ofNullable(getAssociatedTransactionCode()).map(e -> "Zugehöriger Transaktionscode: " + e).orElse(null)
                    )
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
        }

        public String getRecipient() {
            return ObjectUtils.firstNonNull(this.getSenderName(), getSenderEmail(), this.getBankName());
        }

        public TurnoverRecord toTurnOverRecord() {
            return TurnoverRecord.builder()
                    .date(this.getTimestamp().toLocalDate())
                    .amountInCents(this.getBruttoAmountInCents())
                    .description(this.getCombinedDescription())
                    .suggestedCategory(null)
                    .recipient(getRecipient())
                    .build();
        }
    }
}
