package de.sky.regular.income.dao;

import de.sky.regular.income.api.Statement;
import de.sky.regular.income.api.StatementPatch;
import de.sky.regular.income.api.StatementTransactionSummary;
import de.sky.regular.income.api.Transaction;
import generated.sky.regular.income.tables.records.BankStatementRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static generated.sky.regular.income.Tables.BANK_STATEMENT;

@Component
public class StatementDAO {
    private final TransactionsDAO transactionsDAO;

    @Autowired
    public StatementDAO(TransactionsDAO transactionsDAO) {
        this.transactionsDAO = Objects.requireNonNull(transactionsDAO);
    }

    public Statement createStatement(DSLContext ctx, UUID id, StatementPatch patch) {
        ZonedDateTime ts = ZonedDateTime.now();

        BankStatementRecord rec = ctx.selectFrom(BANK_STATEMENT)
                .where(BANK_STATEMENT.ID.eq(id))
                .forUpdate()
                .fetchOptional()
                .orElseGet(() -> ctx.newRecord(BANK_STATEMENT));

        rec.setId(id);

        rec.setDateRecord(patch.getDate());
        rec.setAmountBalanceCents(patch.getBalanceInCents());
        rec.setPreviousStatementId(patch.getPreviousStatementId());

        if (rec.getCreatedAt() == null)
            rec.setCreatedAt(ts);

        rec.setUpdatedAt(ts);

        rec.store();

        transactionsDAO.updateTransactionsFor(ctx, id,ts, patch.transactions);

        return readStatement(ctx, id);
    }

    public Statement readStatement(DSLContext ctx, UUID id) {
        BankStatementRecord rec = ctx.selectFrom(BANK_STATEMENT)
                .where(BANK_STATEMENT.ID.eq(id))
                .fetchOne();

        return mapFromRecord(ctx, rec, true, true);
    }

    public List<Statement> readAllStatements(DSLContext ctx) {
        return ctx.selectFrom(BANK_STATEMENT)
                .fetch()
                .map(rec -> mapFromRecord(ctx, rec, false, true));
    }

    public List<Transaction> readTransactionsFor(DSLContext ctx, UUID id) {
        return transactionsDAO.fetchTransactionsForStatement(ctx, id);
    }

    public StatementTransactionSummary fetchSummaryFor(DSLContext ctx, UUID id) {
        return transactionsDAO.fetchStatementSummaryFor(ctx, id);
    }

    private Statement mapFromRecord(DSLContext ctx, BankStatementRecord rec, boolean fetchTransactions, boolean fetchPredecessor) {
        Statement stmt = new Statement();

        stmt.setId(rec.getId());

        stmt.setCreatedAt(rec.getCreatedAt());
        stmt.setUpdatedAt(rec.getUpdatedAt());

        stmt.setDate(rec.getDateRecord());
        stmt.setBalanceInCents(rec.getAmountBalanceCents());

        if (fetchPredecessor) {
            BankStatementRecord previous = ctx.selectFrom(BANK_STATEMENT)
                    .where(BANK_STATEMENT.ID.eq(rec.getPreviousStatementId()))
                    .fetchOne();

            if (previous != null)
                stmt.setPreviousStatement(mapFromRecord(ctx, previous, false, false));
        }

        if (fetchTransactions)
            stmt.setTransactions(transactionsDAO.fetchTransactionsForStatement(ctx, rec.getId()));

        return stmt;
    }

    public boolean existsAnyStatement(DSLContext ctx) {
        return ctx.fetchExists(BANK_STATEMENT);
    }

    public void deleteStatement(DSLContext ctx, UUID id) {
        transactionsDAO.deleteTransactionForStatement(ctx, id);

        ctx.deleteFrom(BANK_STATEMENT)
                .where(BANK_STATEMENT.ID.eq(id))
                .execute();
    }
}
