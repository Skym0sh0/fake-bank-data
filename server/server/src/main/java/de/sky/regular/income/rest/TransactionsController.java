package de.sky.regular.income.rest;

import de.sky.regular.income.api.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/transactions")
public class TransactionsController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionsController.class);

    private final List<Transaction> transactions = new ArrayList<>();

    public TransactionsController() {
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

    @PostMapping
    public Transaction createTransaction(HttpServletResponse response, @RequestBody Transaction t) {
        logger.info("Create {}", t);

        transactions.add(t);

        response.setStatus(HttpServletResponse.SC_CREATED);

        return t;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        logger.info("read all");

        return transactions;
    }

    @GetMapping("/{id}")
    public Transaction readTransaction(@PathVariable UUID id) {
        logger.info("Read {}", id);

        return transactions.stream()
                .filter(t -> Objects.equals(t.id, id))
                .findAny()
                .orElse(null);
    }

    @PatchMapping("/{id}")
    public Transaction updateTransaction(HttpServletResponse response, @PathVariable UUID id, @RequestBody Transaction t) {
        logger.info("Update {} with {}", id, t);

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

    private static <T> void updateIfSet(Transaction oldTxn, Transaction newTxn, Function<Transaction, T> getter, BiConsumer<Transaction, T> setter) {
        Optional.ofNullable(getter.apply(newTxn))
                .ifPresent(val -> setter.accept(oldTxn, val));
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(HttpServletResponse response, @PathVariable("id") UUID id) {
        logger.info("Delete {}", id);

        transactions.removeIf(t -> Objects.equals(t.id, id));

        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
