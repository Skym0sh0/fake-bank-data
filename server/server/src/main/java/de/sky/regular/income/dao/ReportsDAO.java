package de.sky.regular.income.dao;

import de.sky.regular.income.api.reports.MonthlyIncomeExpenseReport;
import de.sky.regular.income.api.reports.StatementsReport;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.UUID;

import static generated.sky.regular.income.Tables.BANK_STATEMENT;
import static generated.sky.regular.income.Tables.FINANCIAL_TRANSACTION;
import static org.jooq.impl.DSL.*;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class ReportsDAO {
    private static final Logger logger = getLogger(ReportsDAO.class);

    private static final int MAX_NUMBER_RECORDS = 20_000;

    public StatementsReport doStatementsReport(DSLContext ctx, UUID userId, LocalDate begin, LocalDate end) {
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
            logger.warn("Between [{}, {}) were more than {} datapoints", begin, end, MAX_NUMBER_RECORDS);

        return rprt;
    }

    public MonthlyIncomeExpenseReport doMonthlyIncomeExpenseReport(DSLContext ctx, UUID userId) {
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
            logger.warn("There are too many datapoints: {}", MAX_NUMBER_RECORDS);

        return rprt;
    }
}
