package de.sky.regular.income.importing.csv.parsers;

import de.sky.regular.income.api.turnovers.TurnoverImportFormat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public interface TurnoverParser {
    TurnoverImportFormat getSupportedFormat();

    default List<TurnoverRecord> parseCsv(String csv) throws Exception {
        try (var is = new ByteArrayInputStream(csv.getBytes())) {
            return parseCsv(is);
        }
    }

    List<TurnoverRecord> parseCsv(InputStream is) throws Exception;
}
