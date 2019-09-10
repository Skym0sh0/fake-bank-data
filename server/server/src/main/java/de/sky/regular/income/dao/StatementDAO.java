package de.sky.regular.income.dao;

import de.sky.regular.income.api.Statement;
import generated.sky.regular.income.tables.records.BankStatementRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

import static generated.sky.regular.income.Tables.BANK_STATEMENT;

@Component
@Repository
public class StatementDAO {
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
}
