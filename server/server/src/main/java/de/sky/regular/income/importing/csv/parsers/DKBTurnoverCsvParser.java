package de.sky.regular.income.importing.csv.parsers;

import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import de.sky.regular.income.api.turnovers.TurnoverImportFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class DKBTurnoverCsvParser implements TurnoverParser {
    @Override
    public TurnoverImportFormat getSupportedFormat() {
        return TurnoverImportFormat.DKB;
    }

    @Override
    public List<TurnoverRecord> parseCsv(InputStream rowIs) throws Exception {
        try (var is = skipBeginningRubbish(rowIs)) {
            log.info("Preparing CSV parser...");
            var proc = new BeanListProcessor<>(DKBRecord.class, 1000);

            var settings = new CsvParserSettings();
            settings.setHeaderExtractionEnabled(true);
            settings.setProcessor(proc);
            settings.setDelimiterDetectionEnabled(true);
            settings.setLineSeparatorDetectionEnabled(true);
            settings.setQuoteDetectionEnabled(true);

            var parser = new CsvParser(settings);

            log.info("Parsing CSV...");

            parser.parse(is);

            log.info("CSV parsed successfully");

            var result = proc.getBeans();

            log.info("Found {} records", result.size());

            return result.stream()
                    .filter(r -> !Objects.equals(r.getType(), "Abschluss"))
                    .map(DKBRecord::toTurnOverRecord)
                    .toList();
        }
    }

    private Reader skipBeginningRubbish(InputStream is) throws Exception {
        var buffered = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8), 8192);

        while (true) {
            buffered.mark(1024);

            var line = buffered.readLine();
            if (line == null)
                return buffered;

            if (line.contains("Buchungstag") && line.contains("Wertstellung")) {
                buffered.reset();
                return buffered;
            }
        }
    }

    @Data
    public static class DKBRecord {
        @Parsed(field = "Buchungstag")
        @Convert(conversionClass = CsvProcessorConverters.DateConverter.class)
        private LocalDate buchungsTag;

        @Parsed(field = "Wertstellung")
        @Convert(conversionClass = CsvProcessorConverters.DateConverter.class)
        private LocalDate date;

        @Parsed(field = "Buchungstext")
        private String type;

        @Parsed(field = "Auftraggeber / Begünstigter")
        private String owner;


        @Parsed(field = "Kontonummer")
        private String accountNumber;

        @Parsed(field = "BLZ")
        private String blz;

        @Parsed(field = "Betrag (EUR)")
        @Convert(conversionClass = CsvProcessorConverters.MoneyAmountConverter.class)
        private Integer amountInCents;

        @Parsed(field = "Gläubiger-ID")
        private String glaeubiger;

        @Parsed(field = "Mandatsreferenz")
        private String mandatRef;

        @Parsed(field = "Kundenreferenz")
        private String accountRef;

        @Parsed(field = "Verwendungszweck")
        private String description;

        public TurnoverRecord toTurnOverRecord() {
            return TurnoverRecord.builder()
                    .date(getDate())
                    .amountInCents(getAmountInCents())
                    .description(getDescription())
                    .suggestedCategory(null)
                    .recipient(getOwner())
                    .build();
        }
    }
}
