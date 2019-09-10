package de.sky.regular.income.rest;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.Statement;
import de.sky.regular.income.dao.StatementDAO;
import de.sky.regular.income.database.DatabaseSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

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

    @GetMapping
    public List<Statement> getAllStatements() {
        return db.transactionWithResult(dao::readAllStatements);
    }
}
