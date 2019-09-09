package de.sky.regular.income.rest;

import de.sky.regular.income.api.Transaction;
import de.sky.regular.income.dao.TransactionsDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionsController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionsController.class);

    private final TransactionsDAO dao;

    @Autowired
    public TransactionsController(TransactionsDAO dao) {
        this.dao = Objects.requireNonNull(dao);
    }

    @PostMapping
    public Transaction createTransaction(HttpServletResponse response, @RequestBody Transaction t) {
        logger.info("Create {}", t);

        response.setStatus(HttpServletResponse.SC_CREATED);

        return dao.createTransaction(t);
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        logger.info("read all");

        return dao.readAllTransactions();
    }

    @GetMapping("/{id}")
    public Transaction readTransaction(@PathVariable UUID id) {
        logger.info("Read {}", id);

        return dao.readTransaction(id);
    }

    @PatchMapping("/{id}")
    public Transaction updateTransaction(HttpServletResponse response, @PathVariable UUID id, @RequestBody Transaction t) {
        logger.info("Update {} with {}", id, t);

        return dao.patchTransaction(id, t);
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(HttpServletResponse response, @PathVariable("id") UUID id) {
        logger.info("Delete {}", id);

        dao.deleteTransaction(id);

        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
