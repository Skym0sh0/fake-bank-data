package de.sky.regular.income.importing.csv;

import de.sky.regular.income.api.turnovers.TurnoverRow;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class TurnoverCsvImporter {
    private final TurnoverCsvParser csvParser;

    public List<TurnoverRow> parseForPreview(InputStream is) {
        log.info("Parsing file...");

        var records = csvParser.parseCsv(is);

        log.info("Parsed {} turnover records from CSV", records.size());

        return records.stream()
                .map(TurnoverCsvImporter::map)
                .collect(Collectors.toUnmodifiableList());
    }

    private static TurnoverRow map(TurnoverCsvParser.TurnoverRecord rec) {
        return TurnoverRow.builder()
                .date(rec.getDate())
                .amountInCents(rec.getAmountInCents())
                .description(rec.getDescription())
                .suggestedCategory(rec.getCategory())
                .recipient(rec.getRecipient())
                .build();
    }
}
