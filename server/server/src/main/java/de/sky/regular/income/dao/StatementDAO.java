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
