package de.sky.regular.income.dao;

import de.sky.regular.income.api.Transaction;
import generated.sky.regular.income.tables.records.FinancialTransactionRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

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

        if ( rec == null)
            return null;

        updateIfSet(rec, t, FinancialTransactionRecord::getAmountValueCents, Transaction::setAmount);
        updateIfSet(rec, t, FinancialTransactionRecord::getDateRecord, Transaction::setDate);
        updateIfSet(rec, t, FinancialTransactionRecord::getIsPeriodic, Transaction::setIsPeriodic);
        updateIfSet(rec, t, FinancialTransactionRecord::getReason, Transaction::setReason);

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
        t.amount = rec.getAmountValueCents().longValue() / 100.0;
        t.isPeriodic = rec.getIsPeriodic();
        t.reason = rec.getReason();

        return t;
    }

    private static <T> void updateIfSet(FinancialTransactionRecord oldTxn, Transaction newTxn, Function<FinancialTransactionRecord, T> getter, BiConsumer<Transaction, T> setter) {
        Optional.ofNullable(getter.apply(newTxn))
                .ifPresent(val -> setter.accept(oldTxn, val));
    }

    public void deleteTransaction(DSLContext ctx, UUID id) {
        ctx.deleteFrom(FINANCIAL_TRANSACTION)
                .where(FINANCIAL_TRANSACTION.ID.eq(id))
                .execute();
    }
}
