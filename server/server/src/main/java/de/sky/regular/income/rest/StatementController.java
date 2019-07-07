package de.sky.regular.income.rest;

import de.sky.regular.income.api.Statement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/statements")
public class StatementController {
    private final List<Statement> statements = IntStream.range(0, 3)
            .mapToObj(i -> {
                Statement s = new Statement();

                s.setId(UUID.randomUUID());
                s.setDate(LocalDate.now().minusDays(i * 10));
                s.setBalance(100000L - i * 1000L);

                return s;
            })
            .sorted(Comparator.comparing(Statement::getDate))
            .collect(Collectors.toList());

    @GetMapping
    public List<Statement> getAllStatements() {
        return statements;
    }
}
