package de.sky.regular.income.rest;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.reports.IncomeExpenseFlowReport;
import de.sky.regular.income.api.reports.MonthlyIncomeExpenseReport;
import de.sky.regular.income.api.reports.BalanceProgressionReport;
import de.sky.regular.income.dao.IncomeExpenseFlowDataReporter;
import de.sky.regular.income.dao.ReportsDAO;
import de.sky.regular.income.database.DatabaseSupplier;
import de.sky.regular.income.users.UserProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController {
    private static final Logger logger = getLogger(ReportsController.class);

    private final DatabaseConnection db;
    private final ReportsDAO dao;
    private final IncomeExpenseFlowDataReporter flowReporter;
    private final UserProvider user;

    @Autowired
    public ReportsController(DatabaseSupplier supplier, ReportsDAO dao, IncomeExpenseFlowDataReporter flowReporter, UserProvider user) {
        this(supplier.getDatabase(), dao, flowReporter, user);
    }

    @GetMapping("/balance-progression")
    public BalanceProgressionReport fetchBalanceProgressionReport(@RequestParam(value = "begin", required = false) LocalDate begin, @RequestParam(value = "end", required = false) LocalDate end) {
        logger.info("Fetch StatementsReport between [{}, {})", begin, end);

        return db.transactionWithResult(ctx -> dao.doBalanceProgressionReport(ctx, user.getCurrentUser(ctx).getId(), begin, end));
    }

    @GetMapping("/monthly-income-expenses")
    public MonthlyIncomeExpenseReport fetchMonthlyIncomeExpenseReport() {
        logger.info("Fetch monthly IncomeExpenseReport");

        return db.transactionWithResult(ctx -> dao.doMonthlyIncomeExpenseReport(ctx, user.getCurrentUser(ctx).getId()));
    }

    @GetMapping("/income-expenses-flow")
    public IncomeExpenseFlowReport fetchIncomeExpenseFlowReport() {
        logger.info("Fetch IncomeExpense Flow Report");

        return db.transactionWithResult(ctx -> flowReporter.doReport(ctx, user.getCurrentUser(ctx).getId()));
    }
}
