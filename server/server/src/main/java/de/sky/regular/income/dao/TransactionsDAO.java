package de.sky.regular.income.dao;

import de.sky.regular.income.api.StatementTransactionSummary;
import de.sky.regular.income.api.Transaction;
import generated.sky.regular.income.tables.records.FinancialTransactionRecord;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static generated.sky.regular.income.Tables.FINANCIAL_TRANSACTION;

@Component
public class TransactionsDAO {

    public Transaction createTransaction(DSLContext ctx, Transaction t) {
        FinancialTransactionRecord rec = ctx.newRecord(FINANCIAL_TRANSACTION);

        rec.setId(Optional.ofNullable(t.id).orElseGet(UUID::randomUUID));
        rec.setDateRecord(t.date);
        rec.setAmountValueCents((int) (t.amount * 100.0));
        rec.setIsPeriodic(t.isPeriodic);
        rec.setReason(t.reason);

        rec.insert();

        return readTransaction(ctx, rec.getId());
    }

    public Transaction patchTransaction(DSLContext ctx, UUID id, Transaction t) {
        FinancialTransactionRecord rec = ctx.fetchOne(FINANCIAL_TRANSACTION, FINANCIAL_TRANSACTION.ID.eq(id));

        if (rec == null)
            return null;

        updateIfSet(rec, t, Transaction::getAmount, FinancialTransactionRecord::setAmountValueCents);
        updateIfSet(rec, t, Transaction::getDate, FinancialTransactionRecord::setDateRecord);
        updateIfSet(rec, t, Transaction::getIsPeriodic, FinancialTransactionRecord::setIsPeriodic);
        updateIfSet(rec, t, Transaction::getReason, FinancialTransactionRecord::setReason);

        rec.update();

        return readTransaction(ctx, id);
    }

    public List<Transaction> readAllTransactions(DSLContext ctx) {
        return ctx.fetch(FINANCIAL_TRANSACTION)
                .map(TransactionsDAO::map);
    }

    public Transaction readTransaction(DSLContext ctx, UUID id) {
        return map(ctx.fetchOne(FINANCIAL_TRANSACTION, FINANCIAL_TRANSACTION.ID.eq(id)));
    }

    private static Transaction map(FinancialTransactionRecord rec) {
        if (rec == null)
            return null;

        Transaction t = new Transaction();

        t.id = rec.getId();
        t.date = rec.getDateRecord();
        t.amount = rec.getAmountValueCents();
        t.isPeriodic = rec.getIsPeriodic();
        t.reason = rec.getReason();

        return t;
    }

    private static <T> void updateIfSet(FinancialTransactionRecord oldTxn, Transaction newTxn, Function<Transaction, T> getter, BiConsumer<FinancialTransactionRecord, T> setter) {
        Optional.ofNullable(getter.apply(newTxn))
                .ifPresent(val -> setter.accept(oldTxn, val));
    }

    public void deleteTransaction(DSLContext ctx, UUID id) {
        ctx.deleteFrom(FINANCIAL_TRANSACTION)
                .where(FINANCIAL_TRANSACTION.ID.eq(id))
                .execute();
    }

    public StatementTransactionSummary fetchStatementSummaryFor(DSLContext ctx, UUID id) {
        StatementTransactionSummary summary = new StatementTransactionSummary();

        summary.income = fetchSummary(ctx, id, FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS.greaterOrEqual(0));
        summary.expense = fetchSummary(ctx, id, FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS.lessThan(0));
        summary.total = fetchSummary(ctx, id);

        return summary;
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
