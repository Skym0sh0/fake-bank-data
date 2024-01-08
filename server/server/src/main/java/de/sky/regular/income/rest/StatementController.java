package de.sky.regular.income.rest;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.Statement;
import de.sky.regular.income.api.StatementPatch;
import de.sky.regular.income.api.StatementTransactionSummary;
import de.sky.regular.income.api.Transaction;
import de.sky.regular.income.dao.CategoryDAO;
import de.sky.regular.income.dao.StatementDAO;
import de.sky.regular.income.database.DatabaseSupplier;
import de.sky.regular.income.importing.excel.ExcelImporter;
import de.sky.regular.income.users.UserProvider;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class StatementController {
    private final DatabaseConnection db;
    private final StatementDAO statementDAO;
    private final CategoryDAO categoryDAO;
    private final UserProvider user;

    private final ExcelImporter importer;


    @Autowired
    public StatementController(DatabaseSupplier supplier, StatementDAO statementDAO, CategoryDAO categoryDAO, ExcelImporter importer, UserProvider user) {
        this(supplier.getDatabase(), statementDAO, categoryDAO, user, importer);
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

                if (statementDAO.existsAnyStatement(ctx))
                    throw new IllegalArgumentException("Initial Statement can only created if there are no previous statements");
            } else {
                requireNonNull(patch.previousBalanceInCents, "previousBalanceInCents");

                Statement prev = statementDAO.readStatement(ctx, patch.previousStatementId);

                if (!Objects.equals(patch.previousBalanceInCents, prev.finalBalanceInCents))
                    throw new IllegalArgumentException(String.format("Previous balance [%d] must match with previous statement [%d]", patch.previousBalanceInCents, prev.finalBalanceInCents));
            }

            return statementDAO.createStatement(ctx, id, patch);
        });
    }

    @GetMapping
    public List<Statement> getAllStatements() {
        return db.transactionWithResult(statementDAO::readAllStatements);
    }

    @GetMapping("{id}")
    public Statement getStatementByID(@PathVariable("id") UUID id) {
        return db.transactionWithResult(ctx -> statementDAO.readStatement(ctx, id));
    }

    @DeleteMapping("{id}")
    public void deleteStatementByID(@PathVariable("id") UUID id) {
        db.transactionWithoutResult(ctx -> statementDAO.deleteStatement(ctx, id));
    }

    @GetMapping("{id}/summary")
    public StatementTransactionSummary getStatementSummary(@PathVariable("id") UUID id) {
        return db.transactionWithResult(ctx -> statementDAO.fetchSummaryFor(ctx, id));
    }

    @GetMapping("{id}/transactions")
    public List<Transaction> getTransactionsForStatement(@PathVariable("id") UUID id) {
        return db.transactionWithResult(ctx -> statementDAO.readTransactionsFor(ctx, id));
    }

    @PutMapping("/import")
    public void importFile(@RequestParam("file") MultipartFile file) {
        db.transactionWithoutResult(ctx -> {
            if (statementDAO.existsAnyStatement(ctx))
                throw new IllegalStateException("Import can only be used as initial Statement Creation");

            var result = importer.prepareExcelImport(file);

            result.categories.forEach(cat -> categoryDAO.createCategory(ctx, user.getCurrentUser(ctx).getId(), null, cat));
            result.statements.forEach(stmt -> this.statementDAO.createStatement(ctx, stmt.getId(), stmt));
        });
    }
}
