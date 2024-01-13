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

import java.io.Reader;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class VRBankTurnoverCsvParser implements TurnoverParser {
    @Override
    public TurnoverImportFormat getSupportedFormat() {
        return TurnoverImportFormat.VR_BANK_CSV;
    }

    public List<TurnoverRecord> parseCsv(Reader reader) {
        log.info("Preparing CSV parser...");
        var proc = new BeanListProcessor<>(VRBankRecord.class, 1000);

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
                .map(VRBankRecord::toTurnOverRecord)
                .toList();
    }

    @Data
    public static class VRBankRecord {
        @Parsed(field = "Valutadatum")
        @Convert(conversionClass = CsvProcessorConverters.DateConverter.class)
        private LocalDate date;

        @Parsed(field = "Name Zahlungsbeteiligter")
        private String recipient;

        @Parsed(field = "Betrag")
        @Convert(conversionClass = CsvProcessorConverters.MoneyAmountConverter.class)
        private int amountInCents;

        @Parsed(field = "Saldo nach Buchung")
        @Convert(conversionClass = CsvProcessorConverters.MoneyAmountConverter.class)
        private int balanceAfterwards;

        @Parsed(field = "Buchungstext")
        private String bookingType;

        @Parsed(field = "Verwendungszweck")
        private String description;

        @Parsed(field = "Kategorie")
        private String category;

        public TurnoverRecord toTurnOverRecord() {
            return TurnoverRecord.builder()
                    .date(this.getDate())
                    .amountInCents(this.getAmountInCents())
                    .description(this.getDescription())
                    .suggestedCategory(this.getCategory())
                    .recipient(this.getRecipient())
                    .build();
        }
    }
}
