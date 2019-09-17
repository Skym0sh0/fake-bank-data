package de.sky.regular.income.rest;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.Statement;
import de.sky.regular.income.api.StatementPatch;
import de.sky.regular.income.api.StatementTransactionSummary;
import de.sky.regular.income.api.Transaction;
import de.sky.regular.income.dao.StatementDAO;
import de.sky.regular.income.database.DatabaseSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static de.sky.regular.income.rest.Validations.requireNonNull;
import static de.sky.regular.income.rest.Validations.requireNonNullAndEquality;

@RestController
@RequestMapping("/statements")
public class StatementController {
    private final DatabaseConnection db;
    private final StatementDAO dao;

    public StatementController(DatabaseConnection db, StatementDAO dao) {
        this.db = Objects.requireNonNull(db);
        this.dao = Objects.requireNonNull(dao);
    }

    @Autowired
    public StatementController(DatabaseSupplier supplier, StatementDAO dao) {
        this(supplier.getDatabase(), dao);
    }

    @PostMapping("{id}")
    public Statement postStatement(@PathVariable("id") UUID id, @RequestBody StatementPatch patch) {
        requireNonNullAndEquality(id, patch.id);
        requireNonNull(patch.date, "date");
        requireNonNull(patch.balanceInCents, "balanceInCents");

        return db.transactionWithResult(ctx -> {
            if (patch.previousStatementId == null) {
                if (!CollectionUtils.isEmpty(patch.transactions))
                    throw new IllegalArgumentException("Initial Statement must not contain transactions");

                if (dao.existsAnyStatement(ctx))
                    throw new IllegalArgumentException("Initial Statement can only created if there are no previous statements");
            }

            return dao.createStatement(ctx, id, patch);
        });
    }

    @GetMapping("")
    public List<Statement> getAllStatements() {
        return db.transactionWithResult(dao::readAllStatements);
    }

    @GetMapping("{id}")
    public Statement getStatementByID(@PathVariable("id") UUID id) {
        return db.transactionWithResult(ctx -> dao.readStatement(ctx, id));
    }

    @GetMapping("{id}/summary")
    public StatementTransactionSummary getStatementSummary(@PathVariable("id") UUID id) {
        return db.transactionWithResult(ctx -> dao.fetchSummaryFor(ctx, id));
    }

    @GetMapping("{id}/transactions")
    public List<Transaction> getTransactionsForStatement(@PathVariable("id") UUID id) {
        return db.transactionWithResult(ctx -> dao.readTransactionsFor(ctx, id));
    }
}
