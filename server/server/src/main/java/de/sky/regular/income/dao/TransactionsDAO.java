package de.sky.regular.income.dao;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import de.sky.regular.income.api.Reason;
import de.sky.regular.income.api.StatementTransactionSummary;
import de.sky.regular.income.api.Transaction;
import de.sky.regular.income.api.TransactionPatch;
import de.sky.regular.income.utils.TransactionChecksumCalculator;
import generated.sky.regular.income.tables.records.FinancialTransactionRecord;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static generated.sky.regular.income.Tables.FINANCIAL_TRANSACTION;

@Component
public class TransactionsDAO {
    private final TransactionChecksumCalculator checksumCalculator = new TransactionChecksumCalculator();

    public List<Transaction> readAllTransactions(DSLContext ctx) {
        return ctx.fetch(FINANCIAL_TRANSACTION)
                .map(TransactionsDAO::map);
    }

    public Transaction readTransaction(DSLContext ctx, UUID id) {
        return map(ctx.fetchOne(FINANCIAL_TRANSACTION, FINANCIAL_TRANSACTION.ID.eq(id)));
    }

    public void deleteTransactionForStatement(DSLContext ctx, UUID stmtId) {
        ctx.deleteFrom(FINANCIAL_TRANSACTION)
                .where(FINANCIAL_TRANSACTION.BANK_STATEMENT_ID.eq(stmtId))
                .execute();
    }

    public StatementTransactionSummary fetchStatementSummaryFor(DSLContext ctx, UUID id) {
        StatementTransactionSummary summary = new StatementTransactionSummary();

        summary.income = fetchSummary(ctx, id, FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS.greaterOrEqual(0));
        summary.expense = fetchSummary(ctx, id, FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS.lessThan(0));
        summary.total = fetchSummary(ctx, id);

        return summary;
    }

    public void updateTransactionsFor(DSLContext ctx, UUID stmtId, OffsetDateTime timestamp, List<TransactionPatch> transactions) {
        this.deleteTransactionForStatement(ctx, stmtId);

        if (CollectionUtils.isEmpty(transactions))
            return;

        List<FinancialTransactionRecord> transactionRecords = transactions.stream()
                .map(t -> {
                    FinancialTransactionRecord rec = ctx.newRecord(FINANCIAL_TRANSACTION);

                    rec.setId(t.id);

                    rec.setCreatedAt(timestamp);

                    rec.setBankStatementId(stmtId);

                    rec.setAmountValueCents(t.amountInCents);
                    rec.setDateRecord(t.date);
                    //t.dateRank
                    rec.setIsPeriodic(t.isPeriodic);
                    rec.setReason(String.join(";", t.reasons));

                    rec.setChecksum(checksumCalculator.calculateChecksum(t));

                    return rec;
                })
                .collect(Collectors.toList());

        ctx.batchStore(transactionRecords)
                .execute();
    }

    public List<Transaction> fetchTransactionsForStatement(DSLContext ctx, UUID stmtId) {
        return ctx.selectFrom(FINANCIAL_TRANSACTION)
                .where(FINANCIAL_TRANSACTION.BANK_STATEMENT_ID.eq(stmtId))
                .orderBy(FINANCIAL_TRANSACTION.DATE_RECORD.desc())
                .fetch()
                .map(TransactionsDAO::map);
    }

    public List<Reason> fetchReasons(DSLContext ctx) {
        return ctx.selectDistinct(FINANCIAL_TRANSACTION.REASON)
                .from(FINANCIAL_TRANSACTION)
                .fetch()
                .into(String.class)
                .stream()
                .map(str -> {
                    Reason r = new Reason();
                    r.reason = str;
                    return r;
                })
                .collect(Collectors.toList());
    }

    private static Transaction map(FinancialTransactionRecord rec) {
        if (rec == null)
            return null;

        Transaction t = new Transaction();

        t.setId(rec.getId());

        t.setCreatedAt(rec.getCreatedAt().toZonedDateTime());

        t.setDate(rec.getDateRecord());
        // date rank
        t.setAmountInCents(rec.getAmountValueCents());
        t.setIsPeriodic(rec.getIsPeriodic());
        t.setReasons(Arrays.asList(Iterables.toArray(Splitter.on(";").split(rec.getReason()), String.class)));

        return t;
    }

    private static StatementTransactionSummary.Summary fetchSummary(DSLContext ctx, UUID id, Condition... c) {
        Record6<Integer, BigDecimal, BigDecimal, BigDecimal, Integer, Integer> rec = ctx.select(
                DSL.count(),
                DSL.sum(FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS),
                DSL.avg(FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS),
                DSL.median(FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS),
                DSL.min(FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS),
                DSL.max(FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS)
        )
                .from(FINANCIAL_TRANSACTION)
                .where(FINANCIAL_TRANSACTION.BANK_STATEMENT_ID.eq(id))
                .and(DSL.and(c))
                .groupBy(FINANCIAL_TRANSACTION.BANK_STATEMENT_ID)
                .fetchOne();

        if (rec == null) {
            StatementTransactionSummary.Summary sum = new StatementTransactionSummary.Summary();

            sum.count = 0;
            sum.total = 0;
            sum.average = null;
            sum.median = null;
            sum.min = null;
            sum.max = null;

            return sum;
        }

        StatementTransactionSummary.Summary sum = new StatementTransactionSummary.Summary();

        sum.count = rec.value1();
        sum.total = rec.value2().intValue();
        sum.average = rec.value3().intValue();
        sum.median = rec.value4().intValue();
        sum.min = rec.value5();
        sum.max = rec.value6();

        return sum;
    }
}
