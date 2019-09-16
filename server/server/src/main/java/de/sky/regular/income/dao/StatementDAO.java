package de.sky.regular.income.dao;

import de.sky.regular.income.api.Statement;
import de.sky.regular.income.api.StatementTransactionSummary;
import de.sky.regular.income.api.Transaction;
import generated.sky.regular.income.tables.records.BankStatementRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public Statement createStatement(DSLContext ctx, UUID id, Statement stmt) {
        {
            BankStatementRecord rec = ctx.selectFrom(BANK_STATEMENT)
                    .where(BANK_STATEMENT.ID.eq(id))
                    .forUpdate()
                    .fetchOne();

            if (rec != null) {
                rec.setDateRecord(stmt.getDate());
                rec.setAmountBalanceCents(stmt.getBalance());

                // update transactions

                rec.update();

                return map(rec);
            }
        }

        BankStatementRecord rec = ctx.newRecord(BANK_STATEMENT);

        rec.setId(id);
        rec.setDateRecord(stmt.getDate());
        rec.setAmountBalanceCents(stmt.getBalance());

        // update transactions

        rec.insert();

        return map(rec);
    }

    public List<Statement> readAllStatements(DSLContext ctx) {
        return ctx.selectFrom(BANK_STATEMENT)
                .fetch()
                .map(StatementDAO::map);
    }

    private static Statement map(BankStatementRecord rec) {
        Statement stmt = new Statement();

        stmt.id = rec.getId();
        stmt.date = rec.getDateRecord();
        stmt.balance = rec.getAmountBalanceCents();

        return stmt;
    }

    public List<Transaction> readTransactionsFor(DSLContext ctx, UUID id) {
        return transactionsDAO.readAllTransactions(ctx);
    }

    public StatementTransactionSummary fetchSummaryFor(DSLContext ctx, UUID id) {
        return transactionsDAO.fetchStatementSummaryFor(ctx, id);
    }
}
