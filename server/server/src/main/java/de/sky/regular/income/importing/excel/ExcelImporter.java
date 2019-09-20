package de.sky.regular.income.importing.excel;

import de.sky.regular.income.api.StatementPatch;
import de.sky.regular.income.api.TransactionPatch;
import de.sky.regular.income.api.detail.PatchInformation;
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

    public List<StatementPatch> prepareExcelImport(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            List<ExcelRecord> records = parser.parseWorkbookFromStream(is);

            return transform(records);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<StatementPatch> transform(List<ExcelRecord> records) {
        List<StatementPatch> statements = new ArrayList<>();

        List<TransactionPatch> currentTransactions = null;

        for (int i = 0; i < records.size(); i++) {
            ExcelRecord current = records.get(i);

            if (!current.getIsStatement())
                currentTransactions.add(transform(current.getTransaction()));
            else {
                statements.add(transform(current.getStatement(), currentTransactions, last(statements)));

                currentTransactions = new ArrayList<>();
            }
        }

        statements.stream()
                .map(stmt -> String.format("%s -> %d", stmt.getDate(), Optional.ofNullable(stmt.getTransactions()).map(List::size).orElse(null)))
                .forEach(System.out::println);

        return statements;
    }

    private static TransactionPatch transform(ExcelTransactionRecord rec) {
        TransactionPatch t = new TransactionPatch();

        t.setId(UUID.randomUUID());
        t.setDate(rec.getDate());
        t.setAmountInCents(
                Stream.of(rec.getIncome(), rec.getExpense())
                        .filter(Objects::nonNull)
                        .map(d -> d * 100.0)
                        .mapToInt(Double::intValue)
                        .findAny()
                        .orElseThrow()
        );
        t.setIsPeriodic(rec.getPeriodic());
        t.setReasons(Collections.singletonList(rec.getReason()));

        return t;
    }

    private static StatementPatch transform(ExcelBankStatementRecord rec, List<TransactionPatch> transactions, Optional<StatementPatch> predecessor) {
        StatementPatch stmt = new StatementPatch();

        stmt.setId(UUID.randomUUID());
        stmt.setDate(rec.getDate());
        stmt.setBalanceInCents((int) (rec.getBalance() * 100.0));
        stmt.setTransactions(transactions);

        stmt.setPreviousStatementId(predecessor.map(PatchInformation::getId).orElse(null));

        return stmt;
    }

    private static <T> Optional<T> last(List<T> list) {
        if (list.isEmpty())
            return Optional.empty();

        return Optional.ofNullable(list.get(list.size() - 1));
    }
}
