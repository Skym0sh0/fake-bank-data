package de.sky.regular.income.rest;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.Statement;
import de.sky.regular.income.api.StatementPatch;
import de.sky.regular.income.api.StatementTransactionSummary;
import de.sky.regular.income.api.Transaction;
import de.sky.regular.income.dao.StatementDAO;
import de.sky.regular.income.database.DatabaseSupplier;
import de.sky.regular.income.importing.excel.ExcelImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    private final ExcelImporter importer;

    public StatementController(DatabaseConnection db, StatementDAO dao, ExcelImporter importer) {
        this.db = Objects.requireNonNull(db);
        this.dao = Objects.requireNonNull(dao);
        this.importer = Objects.requireNonNull(importer);
    }

    @Autowired
    public StatementController(DatabaseSupplier supplier, StatementDAO dao, ExcelImporter importer) {
        this(supplier.getDatabase(), dao, importer);
    }

    @PostMapping("{id}")
    public Statement postStatement(@PathVariable("id") UUID id, @RequestBody StatementPatch patch) {
        requireNonNullAndEquality(id, patch.id);
        requireNonNull(patch.date, "date");
        requireNonNull(patch.finalBalanceInCents, "finalBalanceInCents");

        return db.transactionWithResult(ctx -> {
            if (patch.previousStatementId == null) {
                if (!CollectionUtils.isEmpty(patch.transactions))
                    throw new IllegalArgumentException("Initial Statement must not contain transactions");

                if (dao.existsAnyStatement(ctx))
                    throw new IllegalArgumentException("Initial Statement can only created if there are no previous statements");
            } else {
                requireNonNull(patch.previousBalanceInCents, "previousBalanceInCents");

                Statement prev = dao.readStatement(ctx, patch.previousStatementId);

                if (!Objects.equals(patch.previousBalanceInCents, prev.finalBalanceInCents))
                    throw new IllegalArgumentException(String.format("Previous balance [%d] must match with previous statement [%d]", patch.previousBalanceInCents, prev.finalBalanceInCents));
            }

            return dao.createStatement(ctx, id, patch);
        });
    }

    @GetMapping
    public List<Statement> getAllStatements() {
        return db.transactionWithResult(dao::readAllStatements);
    }

    @GetMapping("{id}")
    public Statement getStatementByID(@PathVariable("id") UUID id) {
        return db.transactionWithResult(ctx -> dao.readStatement(ctx, id));
    }

    @DeleteMapping("{id}")
    public void deleteStatementByID(@PathVariable("id") UUID id) {
        db.transactionWithoutResult(ctx -> dao.deleteStatement(ctx, id));
    }

    @GetMapping("{id}/summary")
    public StatementTransactionSummary getStatementSummary(@PathVariable("id") UUID id) {
        return db.transactionWithResult(ctx -> dao.fetchSummaryFor(ctx, id));
    }

    @GetMapping("{id}/transactions")
    public List<Transaction> getTransactionsForStatement(@PathVariable("id") UUID id) {
        return db.transactionWithResult(ctx -> dao.readTransactionsFor(ctx, id));
    }

    @PutMapping("/import")
    public void importFile(@RequestParam("file") MultipartFile file) {
        db.transactionWithoutResult(ctx -> {
            if (dao.existsAnyStatement(ctx))
                throw new IllegalStateException("Import can only be used as initial Statement Creation");

            List<StatementPatch> statements = importer.prepareExcelImport(file);

            statements.forEach(stmt -> this.dao.createStatement(ctx, stmt.getId(), stmt));
        });
    }
}
