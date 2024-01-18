package de.sky.regular.income.dao;

import de.sky.regular.income.api.Category;
import de.sky.regular.income.api.reports.IncomeExpenseFlowReport;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.function.TriFunction;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static generated.sky.regular.income.Tables.TURNOVER_ROW;
import static org.jooq.impl.DSL.abs;
import static org.jooq.impl.DSL.sum;

@Service
@RequiredArgsConstructor
public class IncomeExpenseFlowDataReporter {
    private final CategoryDAO categoryDao;

    public IncomeExpenseFlowReport doReport(DSLContext ctx, UUID userId) {
        var categories = categoryDao.fetchCategoryTree(ctx, userId);

        var negativeSumByCategoryId = fetchSumPerCategoryWhere(ctx, userId, TURNOVER_ROW.AMOUNT_VALUE_CENTS.lessThan(0));
        var negative = createDataPoints(categories, negativeSumByCategoryId, (parent, child, amount) -> new IncomeExpenseFlowReport.FlowDataPoint(parent, child, amount));

        var positiveSumByCategoryId = fetchSumPerCategoryWhere(ctx, userId, TURNOVER_ROW.AMOUNT_VALUE_CENTS.greaterThan(0));
        var positive = createDataPoints(categories, positiveSumByCategoryId, (parent, child, amount) -> new IncomeExpenseFlowReport.FlowDataPoint(child, parent, amount));

        var root = new IncomeExpenseFlowReport.FlowDataPoint(
                null,
                null,
                0
        );

        var allDatapoints = Stream.of(negative, positive, List.of(root))
                .flatMap(Collection::stream)
                .toList();

        var now = LocalDate.now();
        return new IncomeExpenseFlowReport(
                now.withDayOfYear(1),
                now.plusYears(1).withDayOfYear(1).minusDays(1),
                allDatapoints
        );
    }

    private static List<IncomeExpenseFlowReport.FlowDataPoint> createDataPoints(List<Category> categories, Map<UUID, Integer> sumByCategoryId, DataPointCreator creator) {
        var costTree = categories.stream()
                .map(c -> CategoryTreeNode.create(c, sumByCategoryId))
                .sorted(Comparator.comparingInt(CategoryTreeNode::amount).reversed())
                .toList();

        var stats = costTree.stream()
                .mapToInt(CategoryTreeNode::amount)
                .summaryStatistics();

        Predicate<CategoryTreeNode> isBigEnough = node -> node.amount() > stats.getMax() * 0.05;

        return costTree.stream()
                .takeWhile(isBigEnough)
//                .limit(25)
                .flatMap(t -> traverse(Integer.MAX_VALUE, null, t, creator))
                .toList();

    }

    private static Map<UUID, Integer> fetchSumPerCategoryWhere(DSLContext ctx, UUID userId, Condition condition) {
        var sumField = sum(abs(TURNOVER_ROW.AMOUNT_VALUE_CENTS)).cast(Integer.class).as("sum");

        return ctx.select(TURNOVER_ROW.CATEGORY_ID, sumField)
                .from(TURNOVER_ROW)
                .where(TURNOVER_ROW.OWNER_ID.eq(userId))
                .and(condition)
                .groupBy(TURNOVER_ROW.CATEGORY_ID)
                .fetch()
                .intoMap(TURNOVER_ROW.CATEGORY_ID, sumField);
    }

    private static Stream<IncomeExpenseFlowReport.FlowDataPoint> traverse(int level, CategoryTreeNode parent, CategoryTreeNode current, DataPointCreator creator) {
        if (level == 0 || current.amount() == 0)
            return Stream.empty();

        var children = current.children()
                .stream()
                .flatMap(child -> traverse(level - 1, current, child, creator));

        var c = creator.apply(
                parent != null ? parent.c().getId() : null,
                current.c().id,
                current.amount()
        );

        return Stream.concat(Stream.of(c), children);
    }

    private record CategoryTreeNode(Category c, List<CategoryTreeNode> children, int amount) {

        public static CategoryTreeNode create(Category c, Map<UUID, Integer> categoriesSumByCategoryId) {
            var children = c.getSubCategories()
                    .stream()
                    .map(child -> create(child, categoriesSumByCategoryId))
                    .sorted(Comparator.comparingInt(CategoryTreeNode::amount).reversed())
                    .toList();

            var childrenAmount = Math.abs(children.stream()
                    .mapToInt(CategoryTreeNode::amount)
                    .sum());

            var currentAmount = categoriesSumByCategoryId.getOrDefault(c.getId(), 0);

            return new CategoryTreeNode(c, children, childrenAmount + currentAmount);
        }
    }

    private interface DataPointCreator extends TriFunction<UUID, UUID, Integer, IncomeExpenseFlowReport.FlowDataPoint> {

    }
}
