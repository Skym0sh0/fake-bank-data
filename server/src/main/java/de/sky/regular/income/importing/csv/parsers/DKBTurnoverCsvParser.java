package de.sky.regular.income.importing.csv.parsers;

import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import de.sky.regular.income.api.TurnoverImportFormat;
import de.sky.regular.income.importing.csv.parsers.common.BeanWithMetaDataProcessor;
import de.sky.regular.income.importing.csv.parsers.common.CsvProcessorConverters;
import de.sky.regular.income.importing.csv.parsers.common.CsvRecordWithMetadata;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableLong;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
public class DKBTurnoverCsvParser implements TurnoverParser {
    @Override
    public TurnoverImportFormat getSupportedFormat() {
        return TurnoverImportFormat.DKB;
    }

    @Override
    public List<TurnoverRecord> parseCsv(Reader rawReader) throws Exception {
        var lineOffset = new MutableLong(0);

        try (var reader = skipBeginningRubbish(rawReader, lineOffset)) {
            log.info("Preparing CSV parser...");
            var proc = new BeanWithMetaDataProcessor<>(DKBRecord.class, lineOffset.getValue());

            var settings = new CsvParserSettings();
            settings.setHeaderExtractionEnabled(true);
            settings.setProcessor(proc);
            settings.setDelimiterDetectionEnabled(true);
            settings.setLineSeparatorDetectionEnabled(true);
            settings.setQuoteDetectionEnabled(true);

            var parser = new CsvParser(settings);

            log.info("Parsing CSV...");

            parser.parse(reader);

            log.info("CSV parsed successfully");

            var result = proc.getRows();

            log.info("Found {} records", result.size());

            return result.stream()
                    .filter(r -> !Objects.equals(r.bean().getType(), "Abschluss"))
                    .map(CsvRecordWithMetadata.map(DKBRecord::toTurnOverRecord))
                    .toList();
        }
    }

    private Reader skipBeginningRubbish(Reader reader, MutableLong offset) throws Exception {
        var buffered = new BufferedReader(reader, 8192);

        while (true) {
            buffered.mark(1024);

            var line = buffered.readLine();
            if (line == null)
                return buffered;

            if (line.contains("Buchungstag") && line.contains("Wertstellung")) {
                buffered.reset();
                return buffered;
            }

            offset.increment();
        }
    }

    @Data
    public static class DKBRecord {
        @Parsed(field = "Buchungstag")
        @Convert(conversionClass = CsvProcessorConverters.FullGermanDateConverter.class)
        private LocalDate buchungsTag;

        @Parsed(field = "Wertstellung")
        @Convert(conversionClass = CsvProcessorConverters.FullGermanDateConverter.class)
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

        public TurnoverRecord toTurnOverRecord(TurnoverRecord.TurnoverRecordBuilder builder) {
            var desc = Stream.of(
                            getDescription(),
                            getMandatRef(),
                            getAccountRef(),
                            getGlaeubiger()
                    )
                    .filter(StringUtils::isNoneBlank)
                    .findFirst()
                    .orElse("<nicht gesetzt>");

            return builder.date(getDate())
                    .amountInCents(getAmountInCents())
                    .description(desc)
                    .suggestedCategory(null)
                    .recipient(getOwner())
                    .build();
        }
    }
}
