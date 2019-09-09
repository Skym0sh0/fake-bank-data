package de.sky.regular.income.dao;

import de.sky.regular.income.api.Statement;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Repository
public class StatementDAO  {
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

    public List<Statement> readAllStatements() {
        return statements;
    }
}
