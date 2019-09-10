package de.sky.regular.income.rest;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.Transaction;
import de.sky.regular.income.dao.TransactionsDAO;
import de.sky.regular.income.database.DatabaseSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionsController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionsController.class);

    private final DatabaseConnection db;
    private final TransactionsDAO dao;

    public TransactionsController(DatabaseConnection db, TransactionsDAO dao) {
        this.db = Objects.requireNonNull(db);
        this.dao = Objects.requireNonNull(dao);
    }

    @Autowired
    public TransactionsController(DatabaseSupplier supplier, TransactionsDAO dao) {
        this(supplier.getDatabase(), dao);
    }

    @PostMapping
    public Transaction createTransaction(HttpServletResponse response, @RequestBody Transaction t) {
        logger.info("Create {}", t);

        response.setStatus(HttpServletResponse.SC_CREATED);

        return db.transactionWithResult(ctx -> dao.createTransaction(ctx, t));
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        logger.info("read all");

        return db.transactionWithResult(dao::readAllTransactions);
    }

    @GetMapping("/{id}")
    public Transaction readTransaction(@PathVariable UUID id) {
        logger.info("Read {}", id);

        return db.transactionWithResult(ctx -> dao.readTransaction(ctx, id));
    }

    @PatchMapping("/{id}")
    public Transaction updateTransaction(HttpServletResponse response, @PathVariable UUID id, @RequestBody Transaction t) {
        logger.info("Update {} with {}", id, t);

        return db.transactionWithResult(ctx -> dao.patchTransaction(ctx, id, t));
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(HttpServletResponse response, @PathVariable("id") UUID id) {
        logger.info("Delete {}", id);

        db.transactionWithoutResult(ctx -> dao.deleteTransaction(ctx, id));

        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
