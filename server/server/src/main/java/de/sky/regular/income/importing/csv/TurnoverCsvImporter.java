package de.sky.regular.income.importing.csv;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.detail.CreatedMetaInformation;
import de.sky.regular.income.api.turnovers.TurnoverImport;
import de.sky.regular.income.api.turnovers.TurnoverImportPatch;
import de.sky.regular.income.api.turnovers.TurnoverRow;
import de.sky.regular.income.database.DatabaseSupplier;
import generated.sky.regular.income.tables.records.TurnoverFileImportRecord;
import generated.sky.regular.income.tables.records.TurnoverRowRecord;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static generated.sky.regular.income.Tables.TURNOVER_FILE_IMPORT;
import static generated.sky.regular.income.Tables.TURNOVER_ROW;

@Service
@AllArgsConstructor
@Slf4j
public class TurnoverCsvImporter {
    private final ChecksumComputer checksummer;

    private final DatabaseConnection db;
    private final TurnoverCsvParser csvParser;
    private final CategorySuggester categorySuggester;

    @Autowired
    public TurnoverCsvImporter(ChecksumComputer checksummer, DatabaseSupplier db, TurnoverCsvParser csvParser, CategorySuggester categorySuggester) {
        this(checksummer, db.getDatabase(), csvParser, categorySuggester);
    }

    public TurnoverImport createImport(ZonedDateTime ts, MultipartFile file, TurnoverImportPatch patch) throws Exception {
        var content = file.getBytes();
        var checksum = checksummer.computeChecksum(content);

        return db.transactionWithResult(ctx -> {
            var imp = ctx.newRecord(TURNOVER_FILE_IMPORT);
            imp.setId(UUID.randomUUID());
            imp.setImportedAt(ts.toOffsetDateTime());

            imp.setFilename(file.getOriginalFilename());
            imp.setFileSize(file.getSize());
            imp.setFileContentType(file.getContentType());
            imp.setFileContent(content);

            imp.setChecksum(checksum);

            imp.insert();

            var rows = patch.getRows()
                    .stream()
                    .map(row -> {
                        var rec = ctx.newRecord(TURNOVER_ROW);

                        rec.setTurnoverFile(imp.getId());

                        rec.setDate(row.getDate());
                        rec.setAmountValueCents(row.getAmountInCents());
                        rec.setDescription(row.getDescription());
                        rec.setSuggestedCategory(row.getSuggestedCategory());
                        rec.setRecipient(row.getRecipient());

                        rec.setCategoryId(row.getCategoryId());

                        rec.setId(row.getChecksum());
                        rec.setChecksum(row.getChecksum());
                        return rec;
                    })
                    .collect(Collectors.toList());

            ctx.batchInsert(rows).execute();

            return fetchTurnoverImport(ctx, imp.getId());
        });
    }

    public TurnoverImport fetchTurnoverImport(UUID id) {
        return db.transactionWithResult(ctx -> fetchTurnoverImport(ctx, id));
    }

    private TurnoverImport fetchTurnoverImport(DSLContext ctx, UUID id) {
        var rec = ctx.fetchOne(TURNOVER_FILE_IMPORT, TURNOVER_FILE_IMPORT.ID.eq(id));

        var rows = ctx.fetch(TURNOVER_ROW, TURNOVER_ROW.TURNOVER_FILE.eq(id));

        return map(rec, rows);
    }

    public List<TurnoverImport> fetchTurnoverImports() {
        return db.transactionWithResult(ctx -> {
            log.trace("Fetching FileImports ...");
            var imports = ctx.fetch(TURNOVER_FILE_IMPORT);

            log.trace("Fetching TurnoverRows ...");
            var rows = ctx.fetch(TURNOVER_ROW).intoGroups(TURNOVER_ROW.TURNOVER_FILE);

            log.debug("Fetched {} file imports with overall {} rows", imports.size(), rows.size());

            return imports.stream()
                    .map(rec -> map(rec, rows.get(rec.getId())))
                    .collect(Collectors.toList());
        });
    }

    public void deleteImport(UUID id) {
        db.transactionWithoutResult(ctx -> {
            ctx.deleteFrom(TURNOVER_ROW)
                    .where(TURNOVER_ROW.TURNOVER_FILE.eq(id))
                    .execute();

            ctx.deleteFrom(TURNOVER_FILE_IMPORT)
                    .where(TURNOVER_FILE_IMPORT.ID.eq(id))
                    .execute();
        });
    }

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

    private static TurnoverImport map(TurnoverFileImportRecord rec, Result<TurnoverRowRecord> rows) {
        var mappedRows = Optional.ofNullable(rows)
                .map(r -> r.map(TurnoverCsvImporter::map))
                .orElse(Collections.emptyList());

        return TurnoverImport.builder()
                .id(rec.getId())
                .importedAt(rec.getImportedAt().atZoneSameInstant(ZoneId.systemDefault()))
                .turnovers(mappedRows)
                .build();
    }

    private static TurnoverRow map(TurnoverRowRecord row) {
        Assert.isTrue(Objects.equals(row.getId(), row.getChecksum()), () -> String.format("Turnover Row must have ID == Checksum (Row: %s)", row.getId()));

        return TurnoverRow.builder()
                .checksum(row.getId())
                .date(row.getDate())
                .description(row.getDescription())
                .amountInCents(row.getAmountValueCents())
                .categoryId(row.getCategoryId())
                .suggestedCategory(row.getSuggestedCategory())
                .recipient(row.getRecipient())
                .build();
    }
}
