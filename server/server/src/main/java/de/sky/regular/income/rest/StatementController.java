package de.sky.regular.income.rest;

import de.sky.regular.income.api.Statement;
import de.sky.regular.income.dao.StatementDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/statements")
public class StatementController {
    private final StatementDAO dao;

    @Autowired
    public StatementController(StatementDAO dao) {
        this.dao = Objects.requireNonNull(dao);
    }

    @GetMapping
    public List<Statement> getAllStatements() {
        return dao.readAllStatements();
    }
}
