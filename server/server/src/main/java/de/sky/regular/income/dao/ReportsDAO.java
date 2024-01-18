package de.sky.regular.income.dao;

import de.sky.regular.income.api.reports.MonthlyIncomeExpenseReport;
import de.sky.regular.income.api.reports.StatementsReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static generated.sky.regular.income.Tables.*;
import static org.jooq.impl.DSL.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportsDAO {
    private static final int MAX_NUMBER_RECORDS = 20_000;

    public StatementsReport doStatementsReport(DSLContext ctx, UUID userId, LocalDate begin, LocalDate end) {
        var start = Optional.ofNullable(begin)
                .orElse(LocalDate.of(1500, Month.JANUARY, 1))
                .atStartOfDay(ZoneId.systemDefault())
                .toOffsetDateTime();
        var ending = Optional.ofNullable(end)
                .orElse(LocalDate.of(3000, Month.JANUARY, 1))
                .atStartOfDay(ZoneId.systemDefault())
                .toOffsetDateTime();

        var rprt = new StatementsReport();

        var balance = sum(TURNOVER_ROW.AMOUNT_VALUE_CENTS).as("BALANCE");
        var balanceOrZero = coalesce(balance, BigDecimal.ZERO);

        var innerTable = table(select(TURNOVER_ROW.TURNOVER_FILE, balance)
                .from(TURNOVER_ROW)
                .where(TURNOVER_ROW.OWNER_ID.eq(userId))
                .groupBy(TURNOVER_ROW.TURNOVER_FILE)
        );

        rprt.data = ctx.select(TURNOVER_FILE_IMPORT.IMPORTED_AT, balanceOrZero)
                .from(TURNOVER_FILE_IMPORT)
                .leftJoin(innerTable)
                .on(TURNOVER_FILE_IMPORT.ID.eq(innerTable.field(TURNOVER_ROW.TURNOVER_FILE)))
                .where(TURNOVER_FILE_IMPORT.OWNER_ID.eq(userId))
                .and(TURNOVER_FILE_IMPORT.IMPORTED_AT.ge(start))
                .and(TURNOVER_FILE_IMPORT.IMPORTED_AT.lessThan(ending))
                .orderBy(TURNOVER_FILE_IMPORT.IMPORTED_AT.asc())
                .limit(MAX_NUMBER_RECORDS + 1)
                .fetch()
                .map(rec -> {
                    var p = new StatementsReport.DataPoint();
                    p.setDate(rec.get(TURNOVER_FILE_IMPORT.IMPORTED_AT).toLocalDate());
                    p.setBalanceInCents(rec.get(balanceOrZero).intValueExact());
                    return p;
                });

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

    public StatementsReport doOldStatementsReport(DSLContext ctx, UUID userId, LocalDate begin, LocalDate end) {
        var rprt = new StatementsReport();

        rprt.data = ctx.select(BANK_STATEMENT.DATE_RECORD, BANK_STATEMENT.AMOUNT_BALANCE_CENTS)
                .from(BANK_STATEMENT)
                .where(and(
                        BANK_STATEMENT.DATE_RECORD.ge(Optional.ofNullable(begin).orElse(LocalDate.of(1500, Month.JANUARY, 1))),
                        BANK_STATEMENT.DATE_RECORD.lessThan(Optional.ofNullable(end).orElse(LocalDate.of(3000, Month.JANUARY, 1)))
                ))
                .and(BANK_STATEMENT.OWNER_ID.eq(userId))
                .orderBy(BANK_STATEMENT.DATE_RECORD.asc())
                .limit(MAX_NUMBER_RECORDS + 1)
                .fetch()
                .map(rec -> {
                    var p = new StatementsReport.DataPoint();
                    p.setDate(rec.get(BANK_STATEMENT.DATE_RECORD));
                    p.setBalanceInCents(rec.get(BANK_STATEMENT.AMOUNT_BALANCE_CENTS));
                    return p;
                });

        if (rprt.data.size() > MAX_NUMBER_RECORDS)
            log.warn("Between [{}, {}) were more than {} datapoints", begin, end, MAX_NUMBER_RECORDS);

        return rprt;
    }

    public MonthlyIncomeExpenseReport doMonthlyIncomeExpenseStatementReport(DSLContext ctx, UUID userId) {
        var date = FINANCIAL_TRANSACTION.DATE_RECORD.as("date");
        var amount = FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS.as("amount");

        var month = trunc(date, DatePart.MONTH);
        var positiveSum = sum(when(amount.greaterOrEqual(0), amount).otherwise(0));
        var negativeSum = sum(when(amount.lessThan(0), amount).otherwise(0));

        MonthlyIncomeExpenseReport rprt = new MonthlyIncomeExpenseReport();

        rprt.data = ctx.select(month, positiveSum, negativeSum)
                .from(
                        select(date, amount)
                                .from(FINANCIAL_TRANSACTION)
                                .where(FINANCIAL_TRANSACTION.OWNER_ID.eq(userId))
                                .unionAll(
                                        select(
                                                BANK_STATEMENT.DATE_RECORD.as(date),
                                                BANK_STATEMENT.AMOUNT_BALANCE_CENTS.as(amount)
                                        )
                                                .from(BANK_STATEMENT)
                                                .where(BANK_STATEMENT.PREVIOUS_STATEMENT_ID.isNull())
                                                .and(BANK_STATEMENT.OWNER_ID.eq(userId))
                                )
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

        if (rprt.data.size() > MAX_NUMBER_RECORDS)
            log.warn("There are too many datapoints: {}", MAX_NUMBER_RECORDS);

        return rprt;
    }
}
