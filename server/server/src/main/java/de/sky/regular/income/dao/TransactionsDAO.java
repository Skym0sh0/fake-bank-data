package de.sky.regular.income.dao;

import de.sky.regular.income.api.*;
import de.sky.regular.income.utils.TransactionChecksumCalculator;
import generated.sky.regular.income.tables.records.FinancialTransactionRecord;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static generated.sky.regular.income.Tables.FINANCIAL_TRANSACTION;
import static org.jooq.impl.DSL.and;

@Service
public class TransactionsDAO {
    private final TransactionChecksumCalculator checksumCalculator = new TransactionChecksumCalculator();

    public List<Transaction> readAllTransactions(DSLContext ctx, UUID userId) {
        return ctx.fetch(FINANCIAL_TRANSACTION, FINANCIAL_TRANSACTION.OWNER_ID.eq(userId))
                .map(TransactionsDAO::map);
    }

    public Transaction readTransaction(DSLContext ctx, UUID userId, UUID id) {
        return map(ctx.fetchOne(FINANCIAL_TRANSACTION, and(
                FINANCIAL_TRANSACTION.ID.eq(id),
                FINANCIAL_TRANSACTION.OWNER_ID.eq(userId)
        )));
    }

    public void deleteTransactionForStatement(DSLContext ctx, UUID userId, UUID stmtId) {
        ctx.deleteFrom(FINANCIAL_TRANSACTION)
                .where(FINANCIAL_TRANSACTION.BANK_STATEMENT_ID.eq(stmtId))
                .and(FINANCIAL_TRANSACTION.OWNER_ID.eq(userId))
                .execute();
    }

    public StatementTransactionSummary fetchStatementSummaryFor(DSLContext ctx, UUID userId, UUID id) {
        StatementTransactionSummary summary = new StatementTransactionSummary();

        summary.income = fetchSummary(ctx, userId, id, FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS.greaterOrEqual(0));
        summary.expense = fetchSummary(ctx, userId, id, FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS.lessThan(0));
        summary.total = fetchSummary(ctx, userId, id);

        return summary;
    }

    public void updateTransactionsFor(DSLContext ctx, UUID userId, UUID stmtId, OffsetDateTime timestamp, List<TransactionPatch> transactions) {
        this.deleteTransactionForStatement(ctx, userId, stmtId);

        if (CollectionUtils.isEmpty(transactions))
            return;

        List<FinancialTransactionRecord> transactionRecords = transactions.stream()
                .map(t -> {
                    FinancialTransactionRecord rec = ctx.newRecord(FINANCIAL_TRANSACTION);

                    rec.setId(t.id);
                    rec.setOwnerId(userId);

                    rec.setCreatedAt(timestamp);

                    rec.setBankStatementId(stmtId);

                    rec.setAmountValueCents(t.amountInCents);
                    rec.setDateRecord(t.date);
                    rec.setIsPeriodic(t.isPeriodic);
                    rec.setReason(null);
                    rec.setCategoryId(t.categoryId);

                    rec.setChecksum(checksumCalculator.calculateChecksum(t));

                    return rec;
                })
                .toList();

        ctx.batchStore(transactionRecords)
                .execute();
    }

    public List<Transaction> fetchTransactionsForStatement(DSLContext ctx, UUID userId, UUID stmtId) {
        return ctx.selectFrom(FINANCIAL_TRANSACTION)
                .where(FINANCIAL_TRANSACTION.BANK_STATEMENT_ID.eq(stmtId))
                .and(FINANCIAL_TRANSACTION.OWNER_ID.eq(userId))
                .orderBy(FINANCIAL_TRANSACTION.DATE_RECORD.desc())
                .fetch()
                .map(TransactionsDAO::map);
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

        t.setCategoryId(rec.getCategoryId());

        return t;
    }

    private static StatisticalSummary fetchSummary(DSLContext ctx, UUID userId, UUID id, Condition... c) {
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
                .and(FINANCIAL_TRANSACTION.OWNER_ID.eq(userId))
                .and(and(c))
                .groupBy(FINANCIAL_TRANSACTION.BANK_STATEMENT_ID)
                .fetchOne();

        if (rec == null) {
            StatisticalSummary sum = new StatisticalSummary();

            sum.count = 0;
            sum.total = 0;
            sum.average = null;
            sum.median = null;
            sum.min = null;
            sum.max = null;

            return sum;
        }

        StatisticalSummary sum = new StatisticalSummary();

        sum.count = rec.value1();
        sum.total = rec.value2().intValue();
        sum.average = rec.value3().intValue();
        sum.median = rec.value4().intValue();
        sum.min = rec.value5();
        sum.max = rec.value6();

        return sum;
    }
}
