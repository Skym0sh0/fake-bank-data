package de.sky.regular.income.dao;

import de.sky.regular.income.api.reports.MonthlyIncomeExpenseReport;
import de.sky.regular.income.api.reports.StatementsReport;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static generated.sky.regular.income.Tables.BANK_STATEMENT;
import static generated.sky.regular.income.Tables.FINANCIAL_TRANSACTION;
import static org.jooq.impl.DSL.*;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class ReportsDAO {
    private static final Logger logger = getLogger(ReportsDAO.class);

    private static final int MAX_NUMBER_RECORDS = 20_000;

    public StatementsReport doStatementsReport(DSLContext ctx, LocalDate begin, LocalDate end) {
        var rprt = new StatementsReport();

        rprt.data = ctx.select(BANK_STATEMENT.DATE_RECORD, BANK_STATEMENT.AMOUNT_BALANCE_CENTS)
                .from(BANK_STATEMENT)
                .where(and(
                        BANK_STATEMENT.DATE_RECORD.ge(Optional.ofNullable(begin).orElse(LocalDate.of(1500, Month.JANUARY, 1))),
                        BANK_STATEMENT.DATE_RECORD.lessThan(Optional.ofNullable(end).orElse(LocalDate.of(3000, Month.JANUARY, 1)))
                ))
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

    public MonthlyIncomeExpenseReport doMonthlyIncomeExpenseReport(DSLContext ctx) {
        var month = trunc(FINANCIAL_TRANSACTION.DATE_RECORD, DatePart.MONTH);
        var positiveSum = sum(when(FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS.greaterOrEqual(0), FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS).otherwise(0));
        var negativeSum = sum(when(FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS.lessThan(0), FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS).otherwise(0));

        MonthlyIncomeExpenseReport rprt = new MonthlyIncomeExpenseReport();

        rprt.data = ctx.select(month, positiveSum, negativeSum)
                .from(FINANCIAL_TRANSACTION)
                .groupBy(month)
                .orderBy(month)
                .limit(MAX_NUMBER_RECORDS + 1)
                .fetch()
                .map(rec -> {
                    var point = new MonthlyIncomeExpenseReport.DataPoint();

                    point.month = rec.get(month);
                    point.incomeInCents = rec.get(positiveSum).intValue();
                    point.expenseInCents = rec.get(negativeSum).intValue();

                    return point;
                });

        if (rprt.data.size() > MAX_NUMBER_RECORDS)
            logger.warn("There are too many datapoints: {}", MAX_NUMBER_RECORDS);

        return rprt;
    }
}
