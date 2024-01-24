package de.sky.regular.income.dao;

import de.sky.regular.income.api.reports.MonthlyIncomeExpenseReport;
import de.sky.regular.income.api.reports.BalanceProgressionReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static generated.sky.regular.income.Tables.TURNOVER_ROW;
import static org.jooq.impl.DSL.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportsDAO {
    private static final int MAX_NUMBER_RECORDS = 20_000;

    public BalanceProgressionReport doBalanceProgressionReport(DSLContext ctx, UUID userId, LocalDate begin, LocalDate end) {
        var start = Optional.ofNullable(begin)
                .orElse(LocalDate.of(1500, Month.JANUARY, 1));
        var ending = Optional.ofNullable(end)
                .orElse(LocalDate.of(3000, Month.JANUARY, 1));

        var sumPerDay = sum(TURNOVER_ROW.AMOUNT_VALUE_CENTS).cast(Integer.class);
        var sumPerDayAliased = sumPerDay.as("sumPerDay");
        var index = DSL.rank().over(DSL.orderBy(TURNOVER_ROW.DATE)).minus(1).as("index");
        var cumulativeSum = DSL.sum(sumPerDay).over(DSL.orderBy(TURNOVER_ROW.DATE)).cast(Integer.class).as("cumulativeSum");

        var rprt = new BalanceProgressionReport();

        rprt.data = ctx.select(
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
                .map(rec -> new BalanceProgressionReport.DataPoint(
                        rec.get(index),
                        rec.get(TURNOVER_ROW.DATE),
                        rec.get(sumPerDayAliased),
                        rec.get(cumulativeSum)
                ));

        if (rprt.data.size() > MAX_NUMBER_RECORDS)
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
                .map(rec -> {
                    var point = new MonthlyIncomeExpenseReport.DataPoint();

                    point.month = rec.get(month);
                    point.incomeInCents = rec.get(positiveSum).intValue();
                    point.expenseInCents = -rec.get(negativeSum).intValue();

                    return point;
                });

        if (monthData.size() > MAX_NUMBER_RECORDS)
            log.warn("There are too many datapoints: {}", MAX_NUMBER_RECORDS);

        var monthsToAdd = new ArrayList<MonthlyIncomeExpenseReport.DataPoint>();
        for (int i = 1; i < monthData.size(); i++) {
            var previous = monthData.get(i - 1).getMonth();
            var current = monthData.get(i).getMonth();

            var months = Period.between(previous, current).toTotalMonths();

            var range = LongStream.range(1, months)
                    .mapToObj(previous::plusMonths)
                    .map(m -> new MonthlyIncomeExpenseReport.DataPoint(m, 0, 0))
                    .toList();

            monthsToAdd.addAll(range);
        }

        return new MonthlyIncomeExpenseReport(
                Stream.concat(monthData.stream(), monthsToAdd.stream())
                        .sorted(Comparator.comparing(MonthlyIncomeExpenseReport.DataPoint::getMonth))
                        .toList()
        );
    }
}
