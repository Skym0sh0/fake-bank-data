package de.sky.regular.income.importing.excel;

import de.sky.regular.income.api.CategoryPatch;
import de.sky.regular.income.api.StatementPatch;
import de.sky.regular.income.api.TransactionPatch;
import de.sky.regular.income.importing.excel.model.ExcelBankStatementRecord;
import de.sky.regular.income.importing.excel.model.ExcelRecord;
import de.sky.regular.income.importing.excel.model.ExcelTransactionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

@Component
public class ExcelImporter {
    private final ExcelParser parser;
    private final ExcelRecordValidator validator;

    @Autowired
    public ExcelImporter(ExcelParser parser, ExcelRecordValidator validator) {
        this.parser = Objects.requireNonNull(parser);
        this.validator = Objects.requireNonNull(validator);
    }

    public ImportResult prepareExcelImport(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            return prepareExcelImport(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ImportResult prepareExcelImport(InputStream is) {
        List<ExcelRecord> records = parser.parseWorkbookFromStream(is);

        return transform(records);
    }

    private ImportResult transform(List<ExcelRecord> records) {
        List<StatementPatch> statements = new ArrayList<>();

        List<TransactionPatch> currentTransactions = null;

        Map<String, CategoryPatch> categoriesByName = new HashMap<>();

        for (ExcelRecord current : records) {
            if (!current.getIsStatement()) {
                TransactionPatch transform = transform(current.getTransaction(), categoriesByName);
                currentTransactions.add(validator.validate(transform));
            } else {
                StatementPatch transform = transform(current.getStatement(), currentTransactions, last(statements));
                statements.add(validator.validate(transform));

                currentTransactions = new ArrayList<>();
            }
        }

        var result = new ImportResult();
        result.categories = categoriesByName.values();
        result.statements = statements;
        return result;
    }

    private static TransactionPatch transform(ExcelTransactionRecord rec, Map<String, CategoryPatch> categoriesByName) {
        TransactionPatch t = new TransactionPatch();

        t.setId(UUID.randomUUID());
        t.setDate(rec.getDate());
        t.setAmountInCents(
                Stream.of(rec.getIncome(), rec.getExpense())
                        .filter(Objects::nonNull)
                        .map(d -> d * 100.0)
                        .map(Math::round)
                        .mapToInt(Number::intValue)
                        .findAny()
                        .orElseThrow()
        );
        t.setIsPeriodic(Optional.ofNullable(rec.getPeriodic()).orElse(false));

        t.setCategoryId(categoriesByName.computeIfAbsent(rec.getReason(), reason -> {
            var patch = new CategoryPatch();
            patch.setId(UUID.randomUUID());
            patch.setName(reason);
            patch.setDescription(reason);
            return patch;
        }).getId());

        return t;
    }

    private static StatementPatch transform(ExcelBankStatementRecord rec, List<TransactionPatch> transactions, Optional<StatementPatch> predecessor) {
        StatementPatch stmt = new StatementPatch();

        stmt.setId(UUID.randomUUID());
        stmt.setDate(rec.getDate());
        stmt.setFinalBalanceInCents((int) Math.round(rec.getBalance() * 100.0));
        stmt.setTransactions(transactions);

        predecessor.ifPresent(pre -> {
            stmt.setPreviousStatementId(pre.getId());
            stmt.setPreviousBalanceInCents(pre.getFinalBalanceInCents());
        });

        return stmt;
    }

    private static <T> Optional<T> last(List<T> list) {
        if (list.isEmpty())
            return Optional.empty();

        return Optional.ofNullable(list.get(list.size() - 1));
    }
}
