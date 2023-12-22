package de.sky.regular.income.importing.csv;

import de.sky.regular.income.api.detail.CreatedMetaInformation;
import de.sky.regular.income.api.turnovers.TurnoverRow;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TurnoverCsvImporter {
    private final ChecksumComputer checksummer;

    private final TurnoverCsvParser csvParser;
    private final CategorySuggester categorySuggester;

    public List<TurnoverRow> parseForPreview(InputStream is) {
        log.info("Parsing file...");

        var records = csvParser.parseCsv(is);

        log.info("Parsed {} turnover records from CSV", records.size());

        try (var suggester = categorySuggester.openForBatchSuggestion()) {
            log.info("Now enriching and mapping CSV rows ...");

            return records.stream()
                    .map(rec -> enrichAndMap(suggester, rec))
                    .sorted(Comparator.comparing(TurnoverRow::getDate))
                    .collect(Collectors.toUnmodifiableList());
        }
    }

    private TurnoverRow enrichAndMap(CategorySuggester.CategoryBatchSuggester categorySuggester, TurnoverCsvParser.TurnoverRecord rec) {
        return TurnoverRow.builder()
                .date(rec.getDate())
                .amountInCents(rec.getAmountInCents())
                .description(rec.getDescription())
                .suggestedCategory(rec.getCategory())
                .recipient(rec.getRecipient())
                .checksum(checksummer.computeChecksum(rec))
                .categoryId(
                        categorySuggester.findCategorySuggestion(rec.getDescription(), rec.getCategory())
                                .map(CreatedMetaInformation::getId)
                                .orElse(null)
                )
                .build();
    }
}
