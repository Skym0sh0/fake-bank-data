package de.sky.regular.income.rest;

import de.sky.regular.income.api.Statement;
import de.sky.regular.income.dao.StatementDAO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statements")
public class StatementController {
    private final StatementDAO dao = new StatementDAO();

    @GetMapping
    public List<Statement> getAllStatements() {
        return dao.readAllStatements();
    }
}
