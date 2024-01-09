package de.sky.regular.income.importing.csv;

import com.google.common.base.Stopwatch;
import de.sky.regular.income.api.turnovers.TurnoverImportFormat;
import de.sky.regular.income.importing.csv.parsers.TurnoverParser;
import de.sky.regular.income.importing.csv.parsers.TurnoverRecord;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class TurnoverFileParser {

    private final Map<TurnoverImportFormat, TurnoverParser> parsers;

    public TurnoverFileParser(List<TurnoverParser> parsers) {
        this.parsers = parsers.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(TurnoverParser::getSupportedFormat, Function.identity()));

        validateParsers(this.parsers);
    }

    @Autowired
    public TurnoverFileParser(TurnoverParser... parsers) {
        this(List.of(parsers));
    }

    @SneakyThrows
    public List<TurnoverRecord> parseCsv(TurnoverImportFormat format, InputStream is) {
        log.info("Parsing file...");

        var parser = parsers.get(format);

        log.info("Using for format {} the parser {}", format, parser.getClass().getName());

        var sw = Stopwatch.createStarted();
        var records = parser.parseCsv(is);

        log.info("Parsed {} records in {}", records.size(), sw.stop());

        return records;
    }

    private static void validateParsers(Map<TurnoverImportFormat, TurnoverParser> parsers) {
        var fails = Stream.of(TurnoverImportFormat.values())
                .filter(frmt -> !parsers.containsKey(frmt))
                .collect(Collectors.toSet());

        if (!fails.isEmpty()) {
            throw new IllegalStateException("Missing support for parser formats: " + fails);
        }
    }
}
