package de.sky.regular.income.importing.csv;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@AllArgsConstructor
@Slf4j
public class TurnoverCsvImporter {
    private final TurnoverCsvParser csvParser;

    public void parseForPreview(InputStream is) {
        log.info("Parsing CSV...");

        var records = csvParser.parseCsv(is);

        log.info("Parsed {} turnover records from CSV", records.size());
    }
}
