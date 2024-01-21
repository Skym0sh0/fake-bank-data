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
import java.io.Reader;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class NewDKBTurnoverCsvParser implements TurnoverParser {
    @Override
    public TurnoverImportFormat getSupportedFormat() {
        return TurnoverImportFormat.NEW_DKB;
    }

    @Override
    public List<TurnoverRecord> parseCsv(Reader rawReader) throws Exception {
        try (var reader = skipBeginningRubbish(rawReader)) {
            log.info("Preparing NewDKB CSV parser...");
            var proc = new BeanListProcessor<>(DKBRecord.class, 1000);

            var settings = new CsvParserSettings();
            settings.setHeaderExtractionEnabled(true);
            settings.setProcessor(proc);
            settings.setDelimiterDetectionEnabled(true);
            settings.setLineSeparatorDetectionEnabled(true);
            settings.setQuoteDetectionEnabled(true);

            var parser = new CsvParser(settings);

            log.info("Parsing NewDKB CSV...");

            parser.parse(reader);

            log.info("NewDKB CSV parsed successfully");

            var result = proc.getBeans();

            log.info("Found {} NewDKB records", result.size());

            return result.stream()
                    .map(DKBRecord::toTurnOverRecord)
                    .toList();
        }
    }

    private Reader skipBeginningRubbish(Reader reader) throws Exception {
        var buffered = new BufferedReader(reader, 8192);

        while (true) {
            buffered.mark(1024);

            var line = buffered.readLine();
            if (line == null)
                return buffered;

            if (line.contains("Buchungsdatum") && line.contains("Wertstellung")) {
                buffered.reset();
                return buffered;
            }
        }
    }

    @Data
    public static class DKBRecord {

        @Parsed(field = "Buchungsdatum")
        @Convert(conversionClass = CsvProcessorConverters.ShortGermanDateConverter.class)
        private LocalDate buchungsdatum;
        @Parsed(field = "Wertstellung")
        @Convert(conversionClass = CsvProcessorConverters.ShortGermanDateConverter.class)
        private LocalDate wertstellung;
        @Parsed(field = "Status")
        private String status;
        @Parsed(field = "Zahlungspflichtige*r")
        private String zahlungspflichtiger;
        @Parsed(field = "Zahlungsempfänger*in")
        private String zahlungsempfaenger;
        @Parsed(field = "Verwendungszweck")
        private String verwendungszweck;
        @Parsed(field = "Umsatztyp")
        private String umsatztyp;
        @Parsed(field = "IBAN")
        private String iban;
        @Parsed(field = "Betrag (€)")
        @Convert(conversionClass = CsvProcessorConverters.MoneyAmountConverter.class)
        private Integer betragEurCent;
        @Parsed(field = "Gläubiger-ID")
        private String glaeubigerID;
        @Parsed(field = "Mandatsreferenz")
        private String mandatsreferenz;
        @Parsed(field = "Kundenreferenz")
        private String kundenreferenz;

        public TurnoverRecord toTurnOverRecord() {
            return TurnoverRecord.builder()
                    .date(getBuchungsdatum())
                    .amountInCents(getBetragEurCent())
                    .description(getVerwendungszweck())
                    .suggestedCategory(null)
                    .recipient(getBetragEurCent() < 0 ? getZahlungsempfaenger() : getZahlungspflichtiger())
                    .build();
        }
    }
}
