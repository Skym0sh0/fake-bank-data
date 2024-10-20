package de.sky.regular.income.importing.csv;

import com.google.common.base.Stopwatch;
import de.sky.regular.income.api.Category;
import de.sky.regular.income.api.CategoryTurnoverReport;
import de.sky.regular.income.api.CategoryTurnoverReportDatapoint;
import de.sky.regular.income.api.RawCsvTable;
import de.sky.regular.income.api.TurnoverImport;
import de.sky.regular.income.api.TurnoverImportFormat;
import de.sky.regular.income.api.TurnoverImportPatch;
import de.sky.regular.income.api.TurnoverImportRowsPatch;
import de.sky.regular.income.api.TurnoverRow;
import de.sky.regular.income.api.TurnoverRowPreview;
import de.sky.regular.income.database.DatabaseConnection;
import de.sky.regular.income.dao.CategoryDAO;
import de.sky.regular.income.database.DatabaseSupplier;
import de.sky.regular.income.importing.csv.parsers.TurnoverRecord;
import de.sky.regular.income.users.UserProvider;
import generated.sky.regular.income.tables.records.TurnoverFileImportRecord;
import generated.sky.regular.income.tables.records.TurnoverRowRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.Reader;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

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
    private final CategoryDAO categoryDao;
    private final CategorySuggester categorySuggester;

    private final UserProvider user;

    @Autowired
    public TurnoverCsvImporter(ChecksumComputer checksummer, DatabaseSupplier db, TurnoverFileParser parser, CategoryDAO categoryDao, CategorySuggester categorySuggester, UserProvider user) {
        this(checksummer, db.getDatabase(), parser, categoryDao, categorySuggester, user);
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
            imp.setTurnoverFileFormat(String.valueOf(patch.getFormat()));
            imp.setFileEncoding(Optional.ofNullable(patch.getEncoding()).orElse("UTF-8"));

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

    public List<de.sky.regular.income.api.TurnoverRow> fetchTurnoversForCategory(UUID categoryId) {
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

    private static Stream<UUID> traverse(Category cat, int level) {
        if (level <= 0)
            return Stream.empty();

        return Stream.concat(
                Stream.of(cat.getId()),
                cat.getSubCategories()
                        .stream()
                        .flatMap(child -> traverse(child, level - 1))
        );
    }

    public CategoryTurnoverReport fetchTurnoversReportForImport(UUID categoryId, DatePart grouping, int recursionLevel) {
        return db.transactionWithResult(ctx -> {
            UUID userId = user.getCurrentUser(ctx).getId();

            var category = categoryDao.fetchById(ctx, userId, categoryId);
            var allCategoryIds = traverse(category, recursionLevel).toList();

            var groupDate = DSL.trunc(TURNOVER_ROW.DATE, grouping);
            var groupIncome = DSL.sum(DSL.when(TURNOVER_ROW.AMOUNT_VALUE_CENTS.greaterOrEqual(0), TURNOVER_ROW.AMOUNT_VALUE_CENTS).otherwise(0)).cast(Integer.class);
            var groupExpense = DSL.sum(DSL.when(TURNOVER_ROW.AMOUNT_VALUE_CENTS.lessThan(0), TURNOVER_ROW.AMOUNT_VALUE_CENTS).otherwise(0)).cast(Integer.class);

            var datapoints = ctx.select(/*TURNOVER_ROW.CATEGORY_ID,*/ groupDate, groupIncome, groupExpense)
                    .from(TURNOVER_ROW)
                    .where(TURNOVER_ROW.OWNER_ID.eq(userId))
                    .and(TURNOVER_ROW.CATEGORY_ID.in(allCategoryIds))
                    .groupBy(/*TURNOVER_ROW.CATEGORY_ID,*/ groupDate)
                    .orderBy(groupDate)
                    .limit(20_000)
                    .fetch()
                    .map(rec -> {
                        var dp = new CategoryTurnoverReportDatapoint();
                        dp.setCategoryId(/*rec.get(TURNOVER_ROW.CATEGORY_ID)*/ null);
                        dp.setDate(rec.get(groupDate));
                        dp.setIncomeAmountInCents(rec.get(groupIncome));
                        dp.setExpenseAmountInCents(rec.get(groupExpense));

                        Optional.ofNullable(category.getBudget())
                                .ifPresent(b -> {
                                    int budget = convertMonthlyTo(b.getBudgetInCents(), grouping, dp.getDate());

                                    dp.setExpenseBudgetInCents(budget);
                                    dp.setExpenseBudgetWarningThresholdInCents((int) Math.round(budget * (1.0 + b.getExceedingThreshold())));
                                });

                        return dp;
                    });

            return CategoryTurnoverReport.builder()
                    .categoryId(categoryId)
                    .datapoints(datapoints)
                    .build();
        });
    }

    private static int convertMonthlyTo(int amount, DatePart d, LocalDate date) {
        return switch (d) {
            case DAY -> amount / YearMonth.from(date).lengthOfMonth();
            case MONTH -> amount;
            case YEAR -> amount * 12;
            default -> throw new UnsupportedOperationException("Unsupported DateRange: " + d);
        };
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

            var updates = patch.getRows().stream()
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

            if (updated != patch.getRows().size())
                throw new RuntimeException("Could not update rows");

            var deleted = ctx.deleteFrom(TURNOVER_ROW)
                    .where(TURNOVER_ROW.ID.in(patch.getDeleteRowIds()))
                    .and(TURNOVER_ROW.OWNER_ID.eq(userId))
                    .and(TURNOVER_ROW.TURNOVER_FILE.eq(id))
                    .execute();

            if (deleted != patch.getDeleteRowIds().size())
                throw new RuntimeException("Could not delete rows");

            return fetchTurnoverImport(ctx, userId, id);
        });
    }

    public void batchPatchTurnoverImport(TurnoverImportRowsPatch patch) {
        var ts = ZonedDateTime.now().toOffsetDateTime();

        db.transactionWithoutResult(ctx -> {
            UUID userId = user.getCurrentUser(ctx).getId();

            var updates = patch.getRows().stream()
                    .map(r ->
                            ctx.update(TURNOVER_ROW)
                                    .set(TURNOVER_ROW.CATEGORY_ID, r.getCategoryId())
                                    .set(TURNOVER_ROW.LAST_UPDATED_AT, ts)
                                    .where(TURNOVER_ROW.ID.eq(r.getId()))
                                    .and(TURNOVER_ROW.OWNER_ID.eq(userId))
                    )
                    .toList();

            var updated = Arrays.stream(ctx.batch(updates).execute()).sum();

            if (updated != patch.getRows().size())
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
                        .sorted(Comparator.comparing(TurnoverRowPreview::getDate))
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
                .id(UUID.randomUUID())
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
                .importedAt(rec.getImportedAt().atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime())
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
