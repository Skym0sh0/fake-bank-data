package de.sky.regular.income.importing.excel;

import de.sky.regular.income.api.StatementPatch;
import de.sky.regular.income.api.TransactionPatch;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
public class ExcelRecordValidator {

    public StatementPatch validate(StatementPatch stmt) {
        validateNonNull(stmt.getDate(), "date");
        validateNonNull(stmt.getFinalBalanceInCents(), "finalBalanceInCents");

        if (stmt.getPreviousStatementId() != null) {
            validateNonNull(stmt.getPreviousBalanceInCents(), "previousBalanceInCents");
            validateNonNull(stmt.getTransactions(), "transactions");

            int sum = Optional.ofNullable(stmt.getTransactions())
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .mapToInt(TransactionPatch::getAmountInCents)
                    .sum();

            if (stmt.previousBalanceInCents + sum != stmt.finalBalanceInCents)
                System.err.printf("%s: Expected %d but was %d + %d = %d Difference=%d%n",stmt.getDate(), stmt.finalBalanceInCents, stmt.previousBalanceInCents, sum, stmt.previousBalanceInCents + sum, stmt.finalBalanceInCents - (stmt.previousBalanceInCents + sum));
        }

        return stmt;
    }

    public TransactionPatch validate(TransactionPatch txn) {
        validateNonNull(txn.getDate(), "date");
        validateNonNull(txn.getAmountInCents(), "amountInCents");
        validateNonEmpty(txn.getReasons(), "reasons");
        validateNonNull(txn.getIsPeriodic(), "isPeriodic");

        return txn;
    }

    private <T extends Collection<U>, U> T validateNonEmpty(T obj, String name) {
        if (obj == null || obj.isEmpty())
            System.err.printf("%s is null or empty%n", name);
        return obj;
    }

    private <T> T validateNonNull(T obj, String name) {
        if (obj == null)
            System.err.printf("%s is null%n", name);

        return obj;
    }
}
