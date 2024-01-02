package de.sky.regular.income.importing.csv;

import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.conversions.ObjectConversion;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import de.sky.regular.income.api.turnovers.TurnoverImportFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class TurnoverCsvParser implements TurnoverParser {
    @Override
    public TurnoverImportFormat getSupportedFormat() {
        return TurnoverImportFormat.VR_BANK_CSV;
    }

    public List<TurnoverRecord> parseCsv(String csv) throws Exception {
        try (var is = new ByteArrayInputStream(csv.getBytes())) {
            return parseCsv(is);
        }
    }

    public List<TurnoverRecord> parseCsv(InputStream is) {
        log.info("Preparing CSV parser...");
        var proc = new BeanListProcessor<>(TurnoverRecord.class, 1000);

        var settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(true);
        settings.setProcessor(proc);
        settings.setDelimiterDetectionEnabled(true);
        settings.setLineSeparatorDetectionEnabled(true);

        var parser = new CsvParser(settings);

        log.info("Parsing CSV...");

        parser.parse(is);

        log.info("CSV parsed successfully");

        var result = proc.getBeans();

        log.info("Found {} records", result.size());

        return Collections.unmodifiableList(result);
    }

    @Data
    public static class TurnoverRecord {
        @Parsed(field = "Valutadatum")
        @Convert(conversionClass = DateConverter.class)
        private LocalDate date;

        @Parsed(field = "Name Zahlungsbeteiligter")
        private String recipient;

        @Parsed(field = "Betrag")
        @Convert(conversionClass = MoneyAmountConverter.class)
        private int amountInCents;

        @Parsed(field = "Saldo nach Buchung")
        @Convert(conversionClass = MoneyAmountConverter.class)
        private int balanceAfterwards;

        @Parsed(field = "Buchungstext")
        private String bookingType;

        @Parsed(field = "Verwendungszweck")
        private String description;

        @Parsed(field = "Kategorie")
        private String category;

        public static class DateConverter extends ObjectConversion<LocalDate> {
            private static final DateTimeFormatter FRMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            @Override
            protected LocalDate fromString(String input) {
                return LocalDate.parse(input, FRMT);
            }

            @Override
            public String revert(LocalDate input) {
                return input.format(FRMT);
            }
        }

        public static class MoneyAmountConverter extends ObjectConversion<Integer> {
            @Override
            protected Integer fromString(String input) {
                double tmp = Double.parseDouble(input.replace(',', '.'));
                return (int) (tmp * 100);
            }

            @Override
            public String revert(Integer input) {
                double tmp = input / 100.0;
                return String.format("%f.2", tmp);
            }
        }
    }
}
