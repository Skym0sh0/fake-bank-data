package de.sky.regular.income.dao;

import de.sky.regular.income.api.Transaction;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public class TransactionsDAO {
    private final List<Transaction> transactions = new ArrayList<>();

    public TransactionsDAO() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        IntStream.range(0, 3)
                .mapToObj(i -> {
                    Transaction t = new Transaction();

                    t.id = UUID.randomUUID();

                    t.amount = Math.round(((Math.random() * 10000) - 5000) * 100.0) / 100.0;
                    t.isPeriodic = rnd.nextBoolean();
                    t.date = LocalDate.now().minusDays(rnd.nextInt(1, 100));
                    t.reason = "Reason " + i;

                    return t;
                })
                .sorted(Comparator.comparing(t -> t.date))
                .forEachOrdered(transactions::add);
    }

    public Transaction createTransaction(Transaction t) {
        transactions.add(t);

        return t;
    }

    public Transaction patchTransaction(UUID id, Transaction t) {
        Transaction old = this.transactions.stream()
                .filter(tr -> Objects.equals(tr.id, id))
                .findAny()
                .orElseThrow();

        updateIfSet(old, t, Transaction::getAmount, Transaction::setAmount);
        updateIfSet(old, t, Transaction::getDate, Transaction::setDate);
        updateIfSet(old, t, Transaction::getIsPeriodic, Transaction::setIsPeriodic);
        updateIfSet(old, t, Transaction::getReason, Transaction::setReason);

        return old;
    }

    public List<Transaction> readAllTransactions() {
        return this.transactions;
    }

    public Transaction readTransaction(UUID id) {
        return transactions.stream()
                .filter(t -> Objects.equals(t.id, id))
                .findAny()
                .orElse(null);
    }

    private static <T> void updateIfSet(Transaction oldTxn, Transaction newTxn, Function<Transaction, T> getter, BiConsumer<Transaction, T> setter) {
        Optional.ofNullable(getter.apply(newTxn))
                .ifPresent(val -> setter.accept(oldTxn, val));
    }

    public void deleteTransaction(UUID id) {
        transactions.removeIf(t -> Objects.equals(t.id, id));
    }
}
