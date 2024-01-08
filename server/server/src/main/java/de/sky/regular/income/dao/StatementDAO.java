package de.sky.regular.income.dao;

import de.sky.regular.income.api.Statement;
import de.sky.regular.income.api.StatementPatch;
import de.sky.regular.income.api.StatementTransactionSummary;
import de.sky.regular.income.api.Transaction;
import generated.sky.regular.income.tables.records.BankStatementRecord;
import generated.sky.regular.income.tables.records.VOrderedBankStatementsRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static generated.sky.regular.income.Tables.*;
import static org.jooq.impl.DSL.*;

@Service
public class StatementDAO {
    private final TransactionsDAO transactionsDAO;

    @Autowired
    public StatementDAO(TransactionsDAO transactionsDAO) {
        this.transactionsDAO = Objects.requireNonNull(transactionsDAO);
    }

    public Statement createStatement(DSLContext ctx, UUID userId, UUID id, StatementPatch patch) {
        OffsetDateTime ts = ZonedDateTime.now().toOffsetDateTime();

        BankStatementRecord rec = ctx.selectFrom(BANK_STATEMENT)
                .where(BANK_STATEMENT.ID.eq(id))
                .and(BANK_STATEMENT.OWNER_ID.eq(userId))
                .forUpdate()
                .fetchOptional()
                .orElseGet(() -> ctx.newRecord(BANK_STATEMENT));

        rec.setId(id);
        rec.setOwnerId(userId);

        rec.setDateRecord(patch.getDate());
        rec.setAmountBalanceCents(patch.getFinalBalanceInCents());
        rec.setPreviousStatementId(patch.getPreviousStatementId());

        if (rec.getCreatedAt() == null)
            rec.setCreatedAt(ts);

        rec.setUpdatedAt(ts);

        rec.store();

        transactionsDAO.updateTransactionsFor(ctx, userId, id, ts, patch.transactions);

        shiftOldPredecessorToThis(ctx, userId, id, patch.getPreviousStatementId());

        return readStatement(ctx, userId, id);
    }

    public Statement readStatement(DSLContext ctx, UUID userId, UUID id) {
        BankStatementRecord rec = ctx.selectFrom(BANK_STATEMENT)
                .where(BANK_STATEMENT.ID.eq(id))
                .and(BANK_STATEMENT.OWNER_ID.eq(userId))
                .fetchOne();

        return mapFromRecord(ctx, rec, true, true);
    }

    public List<Statement> readAllStatements(DSLContext ctx, UUID userId) {
        return ctx.selectFrom(V_ORDERED_BANK_STATEMENTS)
                .where(V_ORDERED_BANK_STATEMENTS.OWNER_ID.eq(userId))
                .orderBy(V_ORDERED_BANK_STATEMENTS.RANK.desc())
                .fetch()
                .map(rec -> mapFromRecord(ctx, rec));
    }

    public List<Transaction> readTransactionsFor(DSLContext ctx, UUID userId, UUID id) {
        return transactionsDAO.fetchTransactionsForStatement(ctx, userId, id);
    }

    public StatementTransactionSummary fetchSummaryFor(DSLContext ctx, UUID userId, UUID id) {
        return transactionsDAO.fetchStatementSummaryFor(ctx, userId, id);
    }

    private void shiftOldPredecessorToThis(DSLContext ctx, UUID userId, UUID id, UUID prevId) {
        if (prevId != null)
            ctx.update(BANK_STATEMENT)
                    .set(BANK_STATEMENT.PREVIOUS_STATEMENT_ID, id)
                    .where(BANK_STATEMENT.PREVIOUS_STATEMENT_ID.eq(prevId))
                    .and(BANK_STATEMENT.ID.ne(id))
                    .and(BANK_STATEMENT.OWNER_ID.ne(userId))
                    .execute();
    }

    private Statement mapFromRecord(DSLContext ctx, VOrderedBankStatementsRecord rec) {
        Statement stmt = new Statement();

        stmt.setId(rec.getId());

        stmt.setCreatedAt(rec.getCreatedAt().toZonedDateTime());
        stmt.setUpdatedAt(rec.getUpdatedAt().toZonedDateTime());

        stmt.setDate(rec.getDateRecord());
        stmt.setFinalBalanceInCents(rec.getAmountBalanceCents());

        BankStatementRecord previous = ctx.selectFrom(BANK_STATEMENT)
                .where(BANK_STATEMENT.ID.eq(rec.getPreviousStatementId()))
                .limit(1)
                .fetchOne();

        if (previous != null)
            stmt.setPreviousStatement(mapFromRecord(ctx, previous, false, false));

        stmt.setTransactions(null);

        stmt.setVolumeInCents(fetchVolume(ctx, rec.getOwnerId(), rec.getId()));

        return stmt;
    }

    private Statement mapFromRecord(DSLContext ctx, BankStatementRecord rec, boolean fetchTransactions, boolean fetchPredecessor) {
        Statement stmt = new Statement();

        stmt.setId(rec.getId());

        stmt.setCreatedAt(rec.getCreatedAt().toZonedDateTime());
        stmt.setUpdatedAt(rec.getUpdatedAt().toZonedDateTime());

        stmt.setDate(rec.getDateRecord());
        stmt.setFinalBalanceInCents(rec.getAmountBalanceCents());

        if (fetchPredecessor) {
            BankStatementRecord previous = ctx.selectFrom(BANK_STATEMENT)
                    .where(BANK_STATEMENT.ID.eq(rec.getPreviousStatementId()))
                    .and(BANK_STATEMENT.OWNER_ID.eq(rec.getOwnerId()))
                    .fetchOne();

            if (previous != null)
                stmt.setPreviousStatement(mapFromRecord(ctx, previous, false, false));
        }

        if (fetchTransactions)
            stmt.setTransactions(transactionsDAO.fetchTransactionsForStatement(ctx, rec.getOwnerId(), rec.getId()));

        stmt.setVolumeInCents(fetchVolume(ctx, rec.getOwnerId(), rec.getId()));

        return stmt;
    }

    private int fetchVolume(DSLContext ctx, UUID userId, UUID stmtId) {
        var sum = coalesce(sum(FINANCIAL_TRANSACTION.AMOUNT_VALUE_CENTS), value(0));

        var transactionsVolume = ctx.select(sum)
                .from(FINANCIAL_TRANSACTION)
                .where(FINANCIAL_TRANSACTION.BANK_STATEMENT_ID.eq(stmtId))
                .and(FINANCIAL_TRANSACTION.OWNER_ID.eq(userId))
                .limit(1)
                .fetchOne(sum);

        return transactionsVolume.intValue();
    }

    public boolean existsAnyStatement(DSLContext ctx, UUID userId) {
        return ctx.fetchExists(BANK_STATEMENT, BANK_STATEMENT.OWNER_ID.eq(userId));
    }

    public void deleteStatement(DSLContext ctx, UUID userId, UUID id) {
        transactionsDAO.deleteTransactionForStatement(ctx, userId, id);

        ctx.deleteFrom(BANK_STATEMENT)
                .where(BANK_STATEMENT.ID.eq(id))
                .and(BANK_STATEMENT.OWNER_ID.eq(userId))
                .execute();
    }
}
