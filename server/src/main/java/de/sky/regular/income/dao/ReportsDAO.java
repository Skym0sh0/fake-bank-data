package de.sky.regular.income.dao;

import de.sky.regular.income.api.BalanceDataPoint;
import de.sky.regular.income.api.BalanceProgressionReport;
import de.sky.regular.income.api.BasicCoarseInfo;
import de.sky.regular.income.api.Category;
import de.sky.regular.income.api.MonthlyIncomeExpenseDataPoint;
import de.sky.regular.income.api.MonthlyIncomeExpenseReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.*;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static generated.sky.regular.income.Tables.TURNOVER_ROW;
import static org.jooq.impl.DSL.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportsDAO {
    private static final int MAX_NUMBER_RECORDS = 20_000;

    private final CategoryDAO categoryDao;

    public BasicCoarseInfo fetchCoarseInfos(DSLContext ctx, UUID userId) {
        var rec = ctx.select(
                        countDistinct(TURNOVER_ROW.ID),
                        countDistinct(TURNOVER_ROW.CATEGORY_ID),
                        min(TURNOVER_ROW.DATE),
                        max(TURNOVER_ROW.DATE)
                )
                .from(TURNOVER_ROW)
                .where(TURNOVER_ROW.OWNER_ID.eq(userId))
                .fetchOne();

        if (rec == null)
            throw new RuntimeException("No result found");

        var maxDepth = findMaxDepth(ctx, userId);

        return new BasicCoarseInfo()
                .userId(userId)
                .earliestTransaction(rec.get(min(TURNOVER_ROW.DATE)))
                .latestTransaction(rec.get(max(TURNOVER_ROW.DATE)))
                .numberOfTransactions(rec.get(countDistinct(TURNOVER_ROW.ID)))
                .numberOfUsedCategories(rec.get(countDistinct(TURNOVER_ROW.CATEGORY_ID)))
                .maxDepthOfCategories(maxDepth.orElse(0));
    }

    public BalanceProgressionReport doBalanceProgressionReport(DSLContext ctx, UUID userId, LocalDate begin, LocalDate end) {
        var start = Optional.ofNullable(begin)
                .orElse(LocalDate.of(1500, Month.JANUARY, 1));
        var ending = Optional.ofNullable(end)
                .orElse(LocalDate.of(3000, Month.JANUARY, 1));

        var sumPerDay = sum(TURNOVER_ROW.AMOUNT_VALUE_CENTS).cast(Integer.class);
        var sumPerDayAliased = sumPerDay.as("sumPerDay");
        var index = rank().over(orderBy(TURNOVER_ROW.DATE)).minus(1).as("index");
        var cumulativeSum = sum(sumPerDay).over(orderBy(TURNOVER_ROW.DATE)).cast(Integer.class).as("cumulativeSum");

        var rprt = new BalanceProgressionReport()
                .data(
                        ctx.select(
                                        TURNOVER_ROW.DATE,
                                        sumPerDayAliased,
                                        index,
                                        cumulativeSum
                                )
                                .from(TURNOVER_ROW)
                                .where(TURNOVER_ROW.OWNER_ID.eq(userId))
                                .and(TURNOVER_ROW.DATE.greaterOrEqual(start))
                                .and(TURNOVER_ROW.DATE.lessOrEqual(ending))
                                .groupBy(TURNOVER_ROW.DATE)
                                .orderBy(TURNOVER_ROW.DATE)
                                .limit(MAX_NUMBER_RECORDS + 1)
                                .fetch()
                                .map(rec -> BalanceDataPoint.builder()
                                        .index(rec.get(index))
                                        .date(rec.get(TURNOVER_ROW.DATE))
                                        .perDayBalanceInCents(rec.get(sumPerDayAliased))
                                        .balanceInCents(rec.get(cumulativeSum))
                                        .build()
                                )
                );

        if (rprt.getData().size() > MAX_NUMBER_RECORDS)
            log.warn("Between [{}, {}) were more than {} datapoints", begin, end, MAX_NUMBER_RECORDS);

        return rprt;
    }

    public MonthlyIncomeExpenseReport doMonthlyIncomeExpenseReport(DSLContext ctx, UUID userId) {
        var date = TURNOVER_ROW.DATE.as("date");
        var amount = TURNOVER_ROW.AMOUNT_VALUE_CENTS.as("amount");

        var month = trunc(date, DatePart.MONTH);
        var positiveSum = sum(when(amount.greaterOrEqual(0), amount).otherwise(0));
        var negativeSum = sum(when(amount.lessThan(0), amount).otherwise(0));

        var monthData = ctx.select(month, positiveSum, negativeSum)
                .from(
                        select(date, amount)
                                .from(TURNOVER_ROW)
                                .where(TURNOVER_ROW.OWNER_ID.eq(userId))
                )
                .groupBy(month)
                .orderBy(month)
                .limit(MAX_NUMBER_RECORDS + 1)
                .fetch()
                .map(rec ->
                        MonthlyIncomeExpenseDataPoint.builder()
                                .month(rec.get(month))
                                .incomeInCents(rec.get(positiveSum).intValue())
                                .expenseInCents(-rec.get(negativeSum).intValue())
                                .build()
                );

        if (monthData.size() > MAX_NUMBER_RECORDS)
            log.warn("There are too many datapoints: {}", MAX_NUMBER_RECORDS);

        var monthsToAdd = new ArrayList<MonthlyIncomeExpenseDataPoint>();
        for (int i = 1; i < monthData.size(); i++) {
            var previous = monthData.get(i - 1).getMonth();
            var current = monthData.get(i).getMonth();

            var months = Period.between(previous, current).toTotalMonths();

            var range = LongStream.range(1, months)
                    .mapToObj(previous::plusMonths)
                    .map(m ->
                            MonthlyIncomeExpenseDataPoint.builder()
                                    .month(m)
                                    .incomeInCents(0)
                                    .expenseInCents(0)
                                    .build()
                    )
                    .toList();

            monthsToAdd.addAll(range);
        }

        return new MonthlyIncomeExpenseReport().data(
                Stream.concat(monthData.stream(), monthsToAdd.stream())
                        .sorted(Comparator.comparing(MonthlyIncomeExpenseDataPoint::getMonth))
                        .toList()
        );
    }

    private OptionalInt findMaxDepth(DSLContext ctx, UUID userId) {
        var tree = categoryDao.fetchCategoryTree(ctx, userId);

        return tree.stream()
                .mapToInt(c -> depth(c, 0))
                .max();
    }

    private static int depth(Category cat, int level) {
        return cat.getSubCategories()
                .stream()
                .mapToInt(c -> depth(c, level + 1))
                .max()
                .orElse(level);
    }
}
