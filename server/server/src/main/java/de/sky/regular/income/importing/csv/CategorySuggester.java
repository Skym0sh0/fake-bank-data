package de.sky.regular.income.importing.csv;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.Category;
import de.sky.regular.income.api.turnovers.TurnoverRowPreview;
import de.sky.regular.income.dao.CategoryDAO;
import de.sky.regular.income.database.DatabaseSupplier;
import de.sky.regular.income.importing.csv.parsers.TurnoverRecord;
import de.sky.regular.income.users.UserProvider;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static generated.sky.regular.income.Tables.TURNOVER_ROW;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategorySuggester {

    private final DatabaseConnection db;
    private final CategoryDAO dao;
    private final UserProvider user;

    @Autowired
    public CategorySuggester(DatabaseSupplier supplier, CategoryDAO dao, UserProvider user) {
        this(supplier.getDatabase(), dao, user);
    }

    public CategoryBatchSuggester openForBatchSuggestion(DSLContext ctx) {
        UUID userId = user.getCurrentUser(ctx).getId();

        var allCategories = dao.fetchAllCategoriesFlatted(ctx, userId, false);

        log.info("Fetched {} categories for batch suggestion...", allCategories.size());

        return new CategoryBatchSuggester(ctx, userId, allCategories);
    }

    @RequiredArgsConstructor
    public static class CategoryBatchSuggester implements AutoCloseable {
        private final DSLContext ctx;
        private final UUID userId;

        private final List<Category> categories;

        @Override
        public void close() {
        }

        public List<TurnoverRowPreview.CategorySuggestion> findSuggestions(TurnoverRecord rec, String similarityChecksum) {
            var allSuggestions = Stream.of(
                            findSuggestionsBySimilarity(0, similarityChecksum),
                            findSuggestionsByDescription(1, rec.getDescription()),
                            findSuggestionsByRecipient(2, rec.getRecipient()),
                            findSuggestionsByMoney(3, rec.getAmountInCents()),
                            findSuggestionsByBankSuggestion(4, rec.getSuggestedCategory())
                    )
                    .flatMap(Collection::stream)
                    .toList();

            var sumOfFrequencies = allSuggestions.stream()
                    .mapToInt(TempSuggestion::getFrequency)
                    .sum();

            var groupedByCategory = allSuggestions.stream()
                    .collect(Collectors.groupingBy(TempSuggestion::getCategoryId));

            return groupedByCategory.entrySet()
                    .stream()
                    .map(e -> {
                        var categoryId = e.getKey();
                        var items = e.getValue();

                        var frequency = items.stream()
                                .mapToInt(TempSuggestion::getFrequency)
                                .sum();

                        var origins = items.stream()
                                .sorted(Comparator.comparing(TempSuggestion::getPriority))
                                .map(TempSuggestion::getOrigin)
                                .toList();

                        return new TurnoverRowPreview.CategorySuggestion(
                                categoryId,
                                (double) frequency / (double) sumOfFrequencies,
                                frequency,
                                origins
                        );
                    })
                    .sorted(Comparator.comparing(TurnoverRowPreview.CategorySuggestion::getPrecedence).reversed())
                    .limit(5)
                    .toList();
        }

        private List<TempSuggestion> findSuggestionsBySimilarity(int priority, String similarityChecksum) {
            return findSuggestionsByCondition(TURNOVER_ROW.SIMILARITY_CHECKSUM.eq(similarityChecksum), priority, TURNOVER_ROW.SIMILARITY_CHECKSUM.getUnqualifiedName().last());
        }

        private List<TempSuggestion> findSuggestionsByDescription(int priority, String desc) {
            return findSuggestionsByCondition(TURNOVER_ROW.DESCRIPTION.eq(desc), priority, TURNOVER_ROW.DESCRIPTION.getUnqualifiedName().last());
        }

        private List<TempSuggestion> findSuggestionsByMoney(int priority, Integer amountInCents) {
            return findSuggestionsByCondition(TURNOVER_ROW.AMOUNT_VALUE_CENTS.eq(amountInCents), priority, TURNOVER_ROW.AMOUNT_VALUE_CENTS.getUnqualifiedName().last());
        }

        private List<TempSuggestion> findSuggestionsByRecipient(int priority, String recipient) {
            return findSuggestionsByCondition(
                    StringUtils.isBlank(recipient)
                            ? TURNOVER_ROW.RECIPIENT.isNull()
                            : TURNOVER_ROW.RECIPIENT.eq(recipient),
                    priority,
                    TURNOVER_ROW.RECIPIENT.getUnqualifiedName().last()
            );
        }

        private List<TempSuggestion> findSuggestionsByBankSuggestion(int priority, String suggestion) {
            if (StringUtils.isBlank(suggestion))
                return List.of();

            return findSuggestionsByCondition(TURNOVER_ROW.SUGGESTED_CATEGORY.eq(suggestion), priority, TURNOVER_ROW.SUGGESTED_CATEGORY.getUnqualifiedName().last());
        }

        private List<TempSuggestion> findSuggestionsByCondition(Condition condition, int priority, String origin) {
            return ctx.select(TURNOVER_ROW.CATEGORY_ID, DSL.count())
                    .from(TURNOVER_ROW)
                    .where(TURNOVER_ROW.OWNER_ID.eq(userId))
                    .and(condition)
                    .groupBy(TURNOVER_ROW.CATEGORY_ID)
                    .orderBy(DSL.count())
                    .limit(3)
                    .fetch()
                    .map(rec -> new TempSuggestion(
                            priority,
                            rec.get(TURNOVER_ROW.CATEGORY_ID),
                            rec.get(DSL.count()),
                            origin
                    ));
        }

        @Value
        private static class TempSuggestion {
            int priority;
            UUID categoryId;
            int frequency;
            String origin;
        }
    }
}
