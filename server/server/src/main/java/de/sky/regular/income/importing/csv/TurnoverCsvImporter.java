package de.sky.regular.income.importing.csv;

import com.google.common.base.Stopwatch;
import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.turnovers.*;
import de.sky.regular.income.database.DatabaseSupplier;
import de.sky.regular.income.importing.csv.parsers.TurnoverRecord;
import de.sky.regular.income.users.UserProvider;
import generated.sky.regular.income.tables.records.TurnoverFileImportRecord;
import generated.sky.regular.income.tables.records.TurnoverRowRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.Reader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static generated.sky.regular.income.Tables.*;
import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.selectFrom;

@Service
@RequiredArgsConstructor
@Slf4j
public class TurnoverCsvImporter {
    private final ChecksumComputer checksummer;

    private final DatabaseConnection db;
    private final TurnoverFileParser parser;
    private final CategorySuggester categorySuggester;

    private final UserProvider user;

    @Autowired
    public TurnoverCsvImporter(ChecksumComputer checksummer, DatabaseSupplier db, TurnoverFileParser parser, CategorySuggester categorySuggester, UserProvider user) {
        this(checksummer, db.getDatabase(), parser, categorySuggester, user);
    }

    public TurnoverImport createImport(ZonedDateTime ts, MultipartFile file, TurnoverImportPatch patch) throws Exception {
        var content = file.getBytes();
        var checksum = checksummer.computeChecksum(content);

        return db.transactionWithResult(ctx -> {
            UUID userId = user.getCurrentUser(ctx).getId();

            var imp = ctx.newRecord(TURNOVER_FILE_IMPORT);
            imp.setId(UUID.randomUUID());
            imp.setImportedAt(ts.toOffsetDateTime());

            imp.setOwnerId(userId);

            imp.setFilename(file.getOriginalFilename());
            imp.setFileSize(file.getSize());
            imp.setFileContentType(file.getContentType());
            imp.setFileContent(content);

            imp.setChecksum(checksum);
            imp.setChecksum(checksum);
            imp.setTurnoverFileFormat(String.valueOf(patch.format));
            imp.setFileEncoding(Optional.ofNullable(patch.encoding).orElse("UTF-8"));

            imp.insert();

            var rows = patch.getRows()
                    .stream()
                    .map(row -> {
                        var rec = ctx.newRecord(TURNOVER_ROW);

                        rec.setId(UUID.randomUUID());
                        rec.setOwnerId(userId);
                        rec.setTurnoverFile(imp.getId());

                        rec.setDate(row.getDate());
                        rec.setAmountValueCents(row.getAmountInCents());
                        rec.setDescription(row.getDescription());
                        rec.setSuggestedCategory(row.getSuggestedCategory());
                        rec.setRecipient(row.getRecipient());

                        rec.setCategoryId(row.getCategoryId());

                        rec.setFullChecksum(row.getChecksum());
                        rec.setSimilarityChecksum(row.getSimilarityChecksum());
                        rec.setLastUpdatedAt(ts.toOffsetDateTime());

                        return rec;
                    })
                    .toList();

            ctx.batchInsert(rows).execute();

            return fetchTurnoverImport(ctx, userId, imp.getId());
        });
    }

    public List<TurnoverRow> fetchTurnoversForImport(UUID categoryId) {
        return db.transactionWithResult(ctx -> {
            UUID userId = user.getCurrentUser(ctx).getId();

            var isValidCategory = ctx.fetchExists(
                    selectFrom(CATEGORY)
                            .where(CATEGORY.ID.eq(categoryId))
                            .and(CATEGORY.OWNER_ID.eq(userId))
            );

            if (!isValidCategory)
                throw new IllegalArgumentException("Category ID is either unknown or belongs to another user: " + categoryId);

            return ctx.selectFrom(TURNOVER_ROW)
                    .where(TURNOVER_ROW.OWNER_ID.eq(userId))
                    .and(TURNOVER_ROW.CATEGORY_ID.eq(categoryId))
                    .orderBy(TURNOVER_ROW.DATE.desc())
                    .fetch()
                    .map(TurnoverCsvImporter::map);
        });
    }

    public TurnoverImport fetchTurnoverImport(UUID id) {
        return db.transactionWithResult(ctx -> fetchTurnoverImport(ctx, user.getCurrentUser(ctx).getId(), id));
    }

    private TurnoverImport fetchTurnoverImport(DSLContext ctx, UUID userId, UUID id) {
        var rec = ctx.fetchOne(TURNOVER_FILE_IMPORT, and(
                TURNOVER_FILE_IMPORT.OWNER_ID.eq(userId),
                TURNOVER_FILE_IMPORT.ID.eq(id)
        ));

        var rows = ctx.fetch(TURNOVER_ROW, and(
                TURNOVER_ROW.OWNER_ID.eq(userId),
                TURNOVER_ROW.TURNOVER_FILE.eq(id)
        ));

        return map(rec, rows);
    }

    public List<TurnoverImport> fetchTurnoverImports() {
        return db.transactionWithResult(ctx -> {
            log.trace("Fetching FileImports ...");
            var imports = ctx.fetch(TURNOVER_FILE_IMPORT, TURNOVER_FILE_IMPORT.OWNER_ID.eq(user.getCurrentUser(ctx).getId()));

            log.trace("Fetching TurnoverRows ...");
            var rows = ctx.fetch(TURNOVER_ROW, TURNOVER_ROW.OWNER_ID.eq(user.getCurrentUser(ctx).getId()))
                    .intoGroups(TURNOVER_ROW.TURNOVER_FILE);

            log.debug("Fetched {} file imports with overall {} rows", imports.size(), rows.size());

            return imports.stream()
                    .map(rec -> map(rec, rows.get(rec.getId())))
                    .sorted(Comparator.comparing(TurnoverImport::getImportedAt).reversed())
                    .toList();
        });
    }

    public TurnoverImport patchTurnoverImport(UUID id, TurnoverImportRowsPatch patch) {
        var ts = ZonedDateTime.now().toOffsetDateTime();

        return db.transactionWithResult(ctx -> {
            UUID userId = user.getCurrentUser(ctx).getId();

            var updates = patch.rows.stream()
                    .map(r ->
                            ctx.update(TURNOVER_ROW)
                                    .set(TURNOVER_ROW.CATEGORY_ID, r.getCategoryId())
                                    .set(TURNOVER_ROW.LAST_UPDATED_AT, ts)
                                    .where(TURNOVER_ROW.ID.eq(r.getId()))
                                    .and(TURNOVER_ROW.OWNER_ID.eq(userId))
                                    .and(TURNOVER_ROW.TURNOVER_FILE.eq(id))
                    )
                    .toList();

            var updated = Arrays.stream(ctx.batch(updates).execute()).sum();

            if (updated != patch.rows.size())
                throw new RuntimeException("Could not update rows");

            return fetchTurnoverImport(ctx, userId, id);
        });
    }

    public void batchPatchTurnoverImport(TurnoverImportRowsPatch patch) {
        var ts = ZonedDateTime.now().toOffsetDateTime();

        db.transactionWithoutResult(ctx -> {
            UUID userId = user.getCurrentUser(ctx).getId();

            var updates = patch.rows.stream()
                    .map(r ->
                            ctx.update(TURNOVER_ROW)
                                    .set(TURNOVER_ROW.CATEGORY_ID, r.getCategoryId())
                                    .set(TURNOVER_ROW.LAST_UPDATED_AT, ts)
                                    .where(TURNOVER_ROW.ID.eq(r.getId()))
                                    .and(TURNOVER_ROW.OWNER_ID.eq(userId))
                    )
                    .toList();

            var updated = Arrays.stream(ctx.batch(updates).execute()).sum();

            if (updated != patch.rows.size())
                throw new RuntimeException("Could not update rows");
        });
    }

    public void deleteImport(UUID id) {
        db.transactionWithoutResult(ctx -> {
            UUID userId = user.getCurrentUser(ctx).getId();

            ctx.deleteFrom(TURNOVER_ROW)
                    .where(TURNOVER_ROW.TURNOVER_FILE.eq(id))
                    .and(TURNOVER_ROW.OWNER_ID.eq(userId))
                    .execute();

            ctx.deleteFrom(TURNOVER_FILE_IMPORT)
                    .where(TURNOVER_FILE_IMPORT.ID.eq(id))
                    .and(TURNOVER_FILE_IMPORT.OWNER_ID.eq(userId))
                    .execute();
        });
    }

    public RawCsvTable parseCsvAsTablePreview(Reader is) {
        return parser.parseRawCsv(is);
    }

    public List<TurnoverRowPreview> parseForPreview(TurnoverImportFormat format, Reader is) {
        var sw = Stopwatch.createStarted();

        log.info("Previewing file ...");

        var records = parser.parseCsv(format, is);

        log.info("Parsed {} turnover records from CSV", records.size());

        return db.transactionWithResult(ctx -> {
            var alreadyExistentRows = findExistentRows(ctx, records);

            try (var suggester = categorySuggester.openForBatchSuggestion(ctx)) {
                log.info("Now enriching and mapping CSV rows ...");

                return records.stream()
                        .parallel()
                        .filter(this::filterInvalidRows)
                        .map(rec -> enrichAndMap(suggester, alreadyExistentRows, rec))
                        .sorted(Comparator.comparing(TurnoverRow::getDate))
                        .toList();
            } finally {
                log.info("Previewing file was done in {}", sw.stop());
            }
        });
    }

    private boolean filterInvalidRows(TurnoverRecord rec) {
        if (rec.getDate() == null || rec.getAmountInCents() == 0)
            return false;
        return true;
    }

    private Set<String> findExistentRows(DSLContext ctx, List<TurnoverRecord> recs) {
        var minDate = recs.stream()
                .filter(Objects::nonNull)
                .map(TurnoverRecord::getDate)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(LocalDate.of(1500, 1, 1));
        var maxDate = recs.stream()
                .filter(Objects::nonNull)
                .map(TurnoverRecord::getDate)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(LocalDate.of(2500, 12, 31));

        return ctx.selectDistinct(TURNOVER_ROW.FULL_CHECKSUM)
                .from(TURNOVER_ROW)
                .where(and(
                        TURNOVER_ROW.DATE.greaterOrEqual(minDate),
                        TURNOVER_ROW.DATE.lessOrEqual(maxDate)
                ))
                .and(TURNOVER_ROW.OWNER_ID.eq(user.getCurrentUser(ctx).getId()))
                .fetch()
                .intoSet(TURNOVER_ROW.FULL_CHECKSUM);
    }

    private TurnoverRowPreview enrichAndMap(CategorySuggester.CategoryBatchSuggester categorySuggester, Set<String> alreadyExistentRowChecksums, TurnoverRecord rec) {
        String checksum = checksummer.computeChecksum(rec);
        String similarityChecksum = checksummer.computeSimilarityChecksum(rec);

        return TurnoverRowPreview.builder()
                .date(rec.getDate())
                .amountInCents(rec.getAmountInCents())
                .description(rec.getDescription())
                .suggestedCategory(null)
                .recipient(rec.getRecipient())
                .checksum(checksum)
                .similarityChecksum(similarityChecksum)
                .categoryId(null)
                .importable(!alreadyExistentRowChecksums.contains(checksum))
                .suggestedCategories(categorySuggester.findSuggestions(rec, similarityChecksum))
                .build();
    }

    private static TurnoverImport map(TurnoverFileImportRecord rec, Result<TurnoverRowRecord> rows) {
        var mappedRows = Optional.ofNullable(rows)
                .map(r -> r.map(TurnoverCsvImporter::map))
                .orElse(Collections.emptyList());

        return TurnoverImport.builder()
                .id(rec.getId())
                .importedAt(rec.getImportedAt().atZoneSameInstant(ZoneId.systemDefault()))
                .filename(rec.getFilename())
                .filesizeBytes(rec.getFileSize())
                .format(TurnoverImportFormat.valueOf(rec.getTurnoverFileFormat()))
                .encoding(rec.getFileEncoding())
                .firstTurnover(mappedRows.stream().map(TurnoverRow::getDate).min(Comparator.naturalOrder()).orElse(null))
                .lastTurnover(mappedRows.stream().map(TurnoverRow::getDate).max(Comparator.naturalOrder()).orElse(null))
                .turnovers(mappedRows)
                .build();
    }

    private static TurnoverRow map(TurnoverRowRecord row) {
        return TurnoverRow.builder()
                .id(row.getId())
                .checksum(row.getFullChecksum())
                .similarityChecksum(row.getSimilarityChecksum())
                .date(row.getDate())
                .description(row.getDescription())
                .amountInCents(row.getAmountValueCents())
                .categoryId(row.getCategoryId())
                .suggestedCategory(row.getSuggestedCategory())
                .recipient(row.getRecipient())
                .build();
    }
}
