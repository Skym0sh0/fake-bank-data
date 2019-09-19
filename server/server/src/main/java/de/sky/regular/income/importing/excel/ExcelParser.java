package de.sky.regular.income.importing.excel;

import de.sky.regular.income.importing.excel.model.ExcelBankStatementRecord;
import de.sky.regular.income.importing.excel.model.ExcelRecord;
import de.sky.regular.income.importing.excel.model.ExcelTransactionRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public class ExcelParser {

    private static final Logger logger = getLogger(ExcelParser.class);

    public Optional<ExcelRecord> parseRow(Row row) {
        if (row == null)
            return Optional.empty();

        try {
            return ExcelColumns.DATE.parseFrom(row, LocalDate.class)
                    .map(date -> {
                        ExcelRecord record = new ExcelRecord();

                        Optional<Double> balance = ExcelColumns.BALANCE.parseFrom(row, Double.class);

                        record.isStatement = balance.isPresent();

                        balance.ifPresent(bal -> {
                            var stmt = new ExcelBankStatementRecord();
                            stmt.date = date;
                            stmt.balance = bal;

                            record.statement = stmt;

                            ExcelColumns.CONTROL.parseFrom(row, Double.class);
                            ExcelColumns.CONTROL_DIFFERENCE.parseFrom(row, Double.class);
                        });

                        if (!record.isStatement) {
                            var transaction = new ExcelTransactionRecord();

                            transaction.date = date;
                            transaction.income = ExcelColumns.INCOME.parseFrom(row, Double.class).orElse(null);
                            transaction.expense = ExcelColumns.EXPENSE.parseFrom(row, Double.class).orElse(null);
                            transaction.periodic = ExcelColumns.PERIODIC.parseFrom(row, Boolean.class).orElse(null);
                            transaction.reason = ExcelColumns.REASON.parseFrom(row, String.class).orElse(null);

                            record.transaction = transaction;
                        }

                        return record;
                    });
        } catch (Exception e) {
            logger.error("Parsing failed:", e);

            return Optional.empty();
        }
    }

    public List<ExcelRecord> parseSheet(Sheet sheet) {
        List<ExcelRecord> records = new ArrayList<>();

        for (var row : sheet)
            this.parseRow(row).ifPresent(records::add);

        return records;
    }

    public List<ExcelRecord> parseWorkbook(Workbook wb) {
        return Optional.ofNullable(wb.getSheet(ExcelColumns.SHEET_NAME))
                .map(this::parseSheet)
                .orElseThrow();
    }

    public List<ExcelRecord> parseWorkbookFromStream(InputStream is) {
        try (var bis = new BufferedInputStream(is)) {
            try (var wb = WorkbookFactory.create(bis)) {
                return this.parseWorkbook(wb);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
