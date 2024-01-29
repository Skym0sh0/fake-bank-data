package de.sky.regular.income.rest;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.reports.BalanceProgressionReport;
import de.sky.regular.income.api.reports.BasicCoarseInfo;
import de.sky.regular.income.api.reports.IncomeExpenseFlowReport;
import de.sky.regular.income.api.reports.MonthlyIncomeExpenseReport;
import de.sky.regular.income.dao.IncomeExpenseFlowDataReporter;
import de.sky.regular.income.dao.ReportsDAO;
import de.sky.regular.income.database.DatabaseSupplier;
import de.sky.regular.income.users.UserProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;

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

    @GetMapping("/info")
    public BasicCoarseInfo fetchCoarseInfos() {
        logger.info("Fetch coarse infos");

        return db.transactionWithResult(ctx -> dao.fetchCoarseInfos(ctx, user.getCurrentUser(ctx).getId()));
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
    public IncomeExpenseFlowReport fetchIncomeExpenseFlowReport(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month
    ) {
        logger.info("Fetch IncomeExpense Flow Report for year={} and month={}", year, month);

        return db.transactionWithResult(ctx -> {
            var begin = LocalDate.of(1900, 1, 1);
            var end = LocalDate.of(3000, 1, 1);

            if (year != null) {
                begin = begin.withYear(year);
                end = begin.plusYears(1);

                if (month != null) {
                    begin = begin.withMonth(month);
                    end = begin.plusMonths(1);
                }
            }

            return flowReporter.doReport(ctx, user.getCurrentUser(ctx).getId(), begin, end);
        });
    }

    @GetMapping("/income-expenses-flow/sliding-window/{unit}/{count}")
    public IncomeExpenseFlowReport fetchIncomeExpenseFlowSlidingWindowReport(
            @PathVariable("unit") String strUnit,
            @PathVariable("count") int count,
            @RequestParam(value = "reference-date", required = false) LocalDate referenceDate
    ) {
        var allowed = Set.of(ChronoUnit.DAYS, ChronoUnit.WEEKS, ChronoUnit.MONTHS, ChronoUnit.YEARS, ChronoUnit.DECADES);

        var unit = ChronoUnit.valueOf(strUnit.toUpperCase());

        if (count < 1)
            throw new IllegalArgumentException("Count must be strictly positive");
        if (!allowed.contains(unit))
            throw new IllegalArgumentException("Unit is not allowed. Only allowed: " + allowed);

        var end = Optional.ofNullable(referenceDate)
                .orElseGet(LocalDate::now);
        var begin = unit.addTo(end, -count);

        logger.info("Fetch IncomeExpense Flow Yearly Sliding Window Report for {} {} between {} and {}", count, unit, begin, end);

        return db.transactionWithResult(ctx -> flowReporter.doSlidingWindowReport(ctx, user.getCurrentUser(ctx).getId(), begin, end, unit));
    }
}
