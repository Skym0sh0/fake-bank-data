package de.sky.regular.income.importing.csv;

import com.google.common.base.Stopwatch;
import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.detail.CreatedMetaInformation;
import de.sky.regular.income.api.turnovers.*;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static generated.sky.regular.income.Tables.TURNOVER_FILE_IMPORT;
import static generated.sky.regular.income.Tables.TURNOVER_ROW;
import static org.jooq.impl.DSL.and;

@Service
@AllArgsConstructor
@Slf4j
public class TurnoverCsvImporter {
    private final ChecksumComputer checksummer;

    private final DatabaseConnection db;
    private final TurnoverFileParser parser;
    private final CategorySuggester categorySuggester;

    @Autowired
    public TurnoverCsvImporter(ChecksumComputer checksummer, DatabaseSupplier db, TurnoverFileParser parser, CategorySuggester categorySuggester) {
        this(checksummer, db.getDatabase(), parser, categorySuggester);
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
            imp.setTurnoverFileFormat(String.valueOf(patch.format));

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
                        rec.setLastUpdatedAt(ts.toOffsetDateTime());

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

    public List<TurnoverRowPreview> parseForPreview(TurnoverImportFormat format, InputStream is) {
        var sw = Stopwatch.createStarted();

        log.info("Previewing file ...");

        var records = parser.parseCsv(format, is);

        var alreadyExistentRows = findExistentRows(records);

        log.info("Parsed {} turnover records from CSV", records.size());

        try (var suggester = categorySuggester.openForBatchSuggestion()) {
            log.info("Now enriching and mapping CSV rows ...");

            return records.stream()
                    .map(rec -> enrichAndMap(suggester, alreadyExistentRows, rec))
                    .sorted(Comparator.comparing(TurnoverRow::getDate))
                    .collect(Collectors.toUnmodifiableList());
        } finally {
            log.info("Previewing file was done in {}", sw.stop());
        }
    }

    private Set<String> findExistentRows(List<TurnoverCsvParser.TurnoverRecord> recs) {
        var minDate = recs.stream()
                .map(TurnoverCsvParser.TurnoverRecord::getDate)
                .min(Comparator.naturalOrder())
                .orElse(LocalDate.MIN);
        var maxDate = recs.stream()
                .map(TurnoverCsvParser.TurnoverRecord::getDate)
                .max(Comparator.naturalOrder())
                .orElse(LocalDate.MAX);

        return db.transactionWithResult(ctx ->
                ctx.selectDistinct(TURNOVER_ROW.CHECKSUM)
                        .from(TURNOVER_ROW)
                        .where(and(
                                TURNOVER_ROW.DATE.greaterOrEqual(minDate),
                                TURNOVER_ROW.DATE.lessOrEqual(maxDate)
                        ))
                        .fetch()
                        .intoSet(TURNOVER_ROW.CHECKSUM)
        );
    }

    private TurnoverRowPreview enrichAndMap(CategorySuggester.CategoryBatchSuggester categorySuggester, Set<String> alreadyExistentRowChecksums, TurnoverCsvParser.TurnoverRecord rec) {
        String checksum = checksummer.computeChecksum(rec);

        return TurnoverRowPreview.builder()
                .date(rec.getDate())
                .amountInCents(rec.getAmountInCents())
                .description(rec.getDescription())
                .suggestedCategory(rec.getCategory())
                .recipient(rec.getRecipient())
                .checksum(checksum)
                .categoryId(
                        categorySuggester.findCategorySuggestion(rec.getDescription(), rec.getCategory())
                                .map(CreatedMetaInformation::getId)
                                .orElse(null)
                )
                .importable(!alreadyExistentRowChecksums.contains(checksum))
                .build();
    }

    private static TurnoverImport map(TurnoverFileImportRecord rec, Result<TurnoverRowRecord> rows) {
        var mappedRows = Optional.ofNullable(rows)
                .map(r -> r.map(TurnoverCsvImporter::map))
                .orElse(Collections.emptyList());

        return TurnoverImport.builder()
                .id(rec.getId())
                .format(TurnoverImportFormat.valueOf(rec.getTurnoverFileFormat()))
                .importedAt(rec.getImportedAt().atZoneSameInstant(ZoneId.systemDefault()))
                .firstTurnover(mappedRows.stream().map(TurnoverRow::getDate).min(Comparator.naturalOrder()).orElse(null))
                .lastTurnover(mappedRows.stream().map(TurnoverRow::getDate).max(Comparator.naturalOrder()).orElse(null))
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
