package de.sky.regular.income.dao;

import de.sky.regular.income.api.Category;
import de.sky.regular.income.api.reports.IncomeExpenseFlowReport;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.function.TriFunction;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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

    public IncomeExpenseFlowReport doReport(DSLContext ctx, UUID userId, LocalDate begin, LocalDate end, int depth) {
        Assert.isTrue(depth >= 0, "Depth must be positive");

        var categories = categoryDao.fetchCategoryTree(ctx, userId);

        var negativeSumByCategoryId = fetchSumPerCategoryWhere(ctx, userId, begin, end, TURNOVER_ROW.AMOUNT_VALUE_CENTS.lessThan(0));
        var negative = createDataPoints(categories, negativeSumByCategoryId, depth, (parent, child, amount) -> new IncomeExpenseFlowReport.FlowDataPoint(parent, child, amount));

        var positiveSumByCategoryId = fetchSumPerCategoryWhere(ctx, userId, begin, end, TURNOVER_ROW.AMOUNT_VALUE_CENTS.greaterThan(0));
        var positive = createDataPoints(categories, positiveSumByCategoryId, depth, (parent, child, amount) -> new IncomeExpenseFlowReport.FlowDataPoint(child, parent, amount));

        var root = new IncomeExpenseFlowReport.FlowDataPoint(
                null,
                null,
                0,
                depth
        );

        var allDatapoints = Stream.of(List.of(root), positive, negative)
                .flatMap(Collection::stream)
                .map(d -> {
                    d.setDepthLevel(depth - d.getDepthLevel());
                    return d;
                })
                .toList();

        return new IncomeExpenseFlowReport(
                begin,
                end,
                allDatapoints.size() <= 1 ? List.of() : allDatapoints
        );
    }

    private static List<IncomeExpenseFlowReport.FlowDataPoint> createDataPoints(List<Category> categories, Map<UUID, Integer> sumByCategoryId, int depth, DataPointCreator creator) {
        var costTree = categories.stream()
                .map(c -> CategoryTreeNode.create(c, sumByCategoryId))
                .sorted(Comparator.comparingInt(CategoryTreeNode::amount).reversed())
                .toList();

        var stats = costTree.stream()
                .mapToInt(CategoryTreeNode::amount)
                .summaryStatistics();

        Predicate<CategoryTreeNode> isBigEnough = node -> node.amount() > stats.getMax() * 0.05;

        return costTree.stream()
                .filter(isBigEnough)
//                .limit(25)
                .map(t -> traverse(depth, null, t, creator))
                .flatMap(Collection::stream)
                .toList();
    }

    private static Map<UUID, Integer> fetchSumPerCategoryWhere(DSLContext ctx, UUID userId, LocalDate begin, LocalDate end, Condition condition) {
        var sumField = sum(abs(TURNOVER_ROW.AMOUNT_VALUE_CENTS)).cast(Integer.class).as("sum");

        return ctx.select(TURNOVER_ROW.CATEGORY_ID, sumField)
                .from(TURNOVER_ROW)
                .where(TURNOVER_ROW.OWNER_ID.eq(userId))
                .and(TURNOVER_ROW.DATE.greaterOrEqual(begin))
                .and(TURNOVER_ROW.DATE.lessThan(end))
                .and(condition)
                .groupBy(TURNOVER_ROW.CATEGORY_ID)
                .fetch()
                .intoMap(TURNOVER_ROW.CATEGORY_ID, sumField);
    }

    private static List<IncomeExpenseFlowReport.FlowDataPoint> traverse(int level, CategoryTreeNode parent, CategoryTreeNode current, DataPointCreator creator) {
        if (level == 0 || current.amount() == 0)
            return List.of();

        var children = current.children()
                .stream()
                .map(child -> traverse(level - 1, current, child, creator))
                .flatMap(Collection::stream)
                .toList();

        var c = creator.apply(
                Optional.ofNullable(parent).map(CategoryTreeNode::c).map(Category::getName).orElse(null),
                current.c().getName(),
                current.amount()
        );
        c.setDepthLevel(level);

        return Stream.concat(Stream.of(c), children.stream())
                .toList();
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

    private interface DataPointCreator extends TriFunction<String, String, Integer, IncomeExpenseFlowReport.FlowDataPoint> {

    }
}
