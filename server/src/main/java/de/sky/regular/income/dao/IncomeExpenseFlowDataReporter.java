package de.sky.regular.income.dao;

import de.sky.regular.income.api.Category;
import de.sky.regular.income.api.FlowDataPoint;
import de.sky.regular.income.api.IncomeExpenseFlowReport;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

import static generated.sky.regular.income.Tables.TURNOVER_ROW;
import static org.jooq.impl.DSL.sum;

@Service
@RequiredArgsConstructor
public class IncomeExpenseFlowDataReporter {
    private final CategoryDAO categoryDao;

    public IncomeExpenseFlowReport doSlidingWindowReport(DSLContext ctx, UUID userId, LocalDate begin, LocalDate end, ChronoUnit unit) {
        var diff = unit.between(begin, end);

        var rprt = doReport(ctx, userId, begin, end);
        rprt.getFlows().forEach(dp -> {
            var newResult = (double) dp.getAmountInCents() / diff;
            dp.setAmountInCents((int) newResult);
        });

        return rprt;
    }

    public IncomeExpenseFlowReport doReport(DSLContext ctx, UUID userId, LocalDate begin, LocalDate end) {
        var categories = categoryDao.fetchCategoryTree(ctx, userId);

        var negativeSumByCategoryId = fetchSumPerCategoryWhere(ctx, userId, begin, end, TURNOVER_ROW.AMOUNT_VALUE_CENTS.lessThan(0));
        var negative = createDataPoints(categories, negativeSumByCategoryId, (parent, child, level, amount) -> FlowDataPoint.builder()
                .fromCategory(parent)
                .toCategory(child)
                .depthLevel(level)
                .amountInCents(amount)
                .build()
        );

        var positiveSumByCategoryId = fetchSumPerCategoryWhere(ctx, userId, begin, end, TURNOVER_ROW.AMOUNT_VALUE_CENTS.greaterThan(0));
        var positive = createDataPoints(categories, positiveSumByCategoryId, (parent, child, level, amount) -> FlowDataPoint.builder()
                .fromCategory(child)
                .toCategory(parent)
                .depthLevel(level)
                .amountInCents(amount)
                .build()
        );

        var allDatapoints = Stream.of(positive, negative)
                .flatMap(Collection::stream)
                .toList();

        return IncomeExpenseFlowReport.builder()
                .start(begin)
                .end(end)
                .flows(allDatapoints)
                .build();
    }

    private static List<FlowDataPoint> createDataPoints(List<Category> categories, Map<UUID, Integer> sumByCategoryId, DataPointCreator creator) {
        var costTree = categories.stream()
                .map(c -> CategoryTreeNode.create(c, 0, sumByCategoryId))
                .toList();

        var total = costTree.stream()
                .mapToInt(CategoryTreeNode::amount)
                .summaryStatistics()
                .getSum();

        var prunedCostTree = costTree.stream()
                .flatMap(node -> checkAndPrune(node, total))
                .toList();

        return prunedCostTree.stream()
                .map(t -> transformAndFlat(null, t, creator))
                .flatMap(Collection::stream)
                .toList();
    }

    private static Stream<CategoryTreeNode> checkAndPrune(CategoryTreeNode current, double totalSum) {
        var quotient = current.amount() / totalSum;

        if (current.amount() == 0 || quotient < 0.005)
            return Stream.empty();

        var newChildren = current.children()
                .stream()
                .flatMap(child -> checkAndPrune(child, totalSum))
                .sorted(Comparator.comparing((CategoryTreeNode s) -> Math.abs(s.amount())).reversed())
                .toList();

        return Stream.of(current.withChildren(newChildren));
    }

    private static List<FlowDataPoint> transformAndFlat(CategoryTreeNode parent, CategoryTreeNode current, DataPointCreator creator) {
        var children = current.children()
                .stream()
                .map(child -> transformAndFlat(current, child, creator))
                .flatMap(Collection::stream)
                .toList();

        var prefix = current.amount() > 0 ? "+" : "-";

        var c = creator.apply(
                Optional.ofNullable(parent).map(CategoryTreeNode::c).map(Category::getName).map(n -> prefix + n).orElse(null),
                prefix + current.c().getName(),
                current.level(),
                current.amount()
        );

        return Stream.concat(Stream.of(c), children.stream())
                .toList();
    }

    @With
    private record CategoryTreeNode(Category c, int level, List<CategoryTreeNode> children, int amount) {

        public static CategoryTreeNode create(Category c, int level, Map<UUID, Integer> categoriesSumByCategoryId) {
            var children = c.getSubCategories()
                    .stream()
                    .map(child -> create(child, level + 1, categoriesSumByCategoryId))
                    .sorted(Comparator.comparingInt(CategoryTreeNode::amount).reversed())
                    .toList();

            var childrenAmount = children.stream()
                    .mapToInt(CategoryTreeNode::amount)
                    .sum();

            var currentAmount = categoriesSumByCategoryId.getOrDefault(c.getId(), 0);

            return new CategoryTreeNode(c, level, children, childrenAmount + currentAmount);
        }
    }

    private interface DataPointCreator {
        FlowDataPoint apply(String from, String to, int level, int amount);
    }

    private static Map<UUID, Integer> fetchSumPerCategoryWhere(DSLContext ctx, UUID userId, LocalDate begin, LocalDate end, Condition condition) {
        var sumField = sum(TURNOVER_ROW.AMOUNT_VALUE_CENTS).cast(Integer.class).as("sum");

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
}
