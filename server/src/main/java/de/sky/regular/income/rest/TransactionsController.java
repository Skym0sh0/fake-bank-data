package de.sky.regular.income.rest;

import de.sky.regular.income.api.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/transaction")
public class TransactionsController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionsController.class);

    private final List<Transaction> transactions = new ArrayList<>();

    public TransactionsController() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        IntStream.range(0, 3)
                .mapToObj(i -> {
                    Transaction t = new Transaction();

                    t.id = UUID.randomUUID();

                    t.amountInCent = rnd.nextLong(-5000, 5000);
                    t.isPeriodic = rnd.nextBoolean();
                    t.timestamp = LocalDate.now().minusDays(rnd.nextInt(1, 100));
                    t.reason = "Reason " + i;

                    return t;
                })
                .sorted(Comparator.comparing(t -> t.timestamp))
                .forEachOrdered(transactions::add);
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction t) {
        logger.info("Create {}", t);

        transactions.add(t);

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
    public Transaction updateTransaction(@PathVariable UUID id, @RequestBody Transaction t) {
        logger.info("Update {} with {}", id, t);

        deleteTransaction(id);

        return createTransaction(t);
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable("id") UUID id) {
        logger.info("Delete {}", id);

        transactions.removeIf(t -> Objects.equals(t.id, id));
    }
}
