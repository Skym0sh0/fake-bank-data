package de.sky.regular.income.rest;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.reports.StatementsReport;
import de.sky.regular.income.dao.ReportsDAO;
import de.sky.regular.income.database.DatabaseSupplier;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/reports")
public class ReportsController {
    private static final Logger logger = getLogger(ReportsController.class);

    private final DatabaseConnection db;
    private final ReportsDAO dao;

    public ReportsController(DatabaseConnection db, ReportsDAO dao) {
        this.db = Objects.requireNonNull(db);
        this.dao = Objects.requireNonNull(dao);
    }

    @Autowired
    public ReportsController(DatabaseSupplier supplier, ReportsDAO dao) {
        this(supplier.getDatabase(), dao);
    }

    @GetMapping("/statements")
    public StatementsReport fetchStatementsReport(@RequestParam(value = "begin", required = false) LocalDate begin, @RequestParam(value = "end", required = false) LocalDate end) {
        logger.info("Fetch StatementsReport between [{}, {})", begin, end);

        return db.transactionWithResult(ctx -> dao.doStatementsReport(ctx, begin, end));
    }
}
