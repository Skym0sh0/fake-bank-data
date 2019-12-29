package de.sky.regular.income.dao;

import de.sky.regular.income.api.reports.StatementsReport;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static generated.sky.regular.income.Tables.BANK_STATEMENT;
import static org.jooq.impl.DSL.and;
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
}
