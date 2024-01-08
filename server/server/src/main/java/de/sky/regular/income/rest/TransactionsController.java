package de.sky.regular.income.rest;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.Transaction;
import de.sky.regular.income.dao.TransactionsDAO;
import de.sky.regular.income.database.DatabaseSupplier;
import de.sky.regular.income.users.UserProvider;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionsController {
    private final DatabaseConnection db;
    private final TransactionsDAO dao;
    private final UserProvider user;

    @Autowired
    public TransactionsController(DatabaseSupplier supplier, TransactionsDAO dao, UserProvider user) {
        this(supplier.getDatabase(), dao, user);
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return db.transactionWithResult((DSLContext ctx) -> dao.readAllTransactions(ctx, user.getCurrentUser(ctx).getId()));
    }

    @GetMapping("/{id}")
    public Transaction readTransaction(@PathVariable UUID id) {
        return db.transactionWithResult(ctx -> dao.readTransaction(ctx, user.getCurrentUser(ctx).getId(), id));
    }
}
