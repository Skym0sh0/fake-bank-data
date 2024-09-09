package de.sky.regular.income.importing.csv.parsers;

import de.sky.regular.income.api.TurnoverImportFormat;

import java.io.Reader;
import java.util.List;

public interface TurnoverParser {
    TurnoverImportFormat getSupportedFormat();

    List<TurnoverRecord> parseCsv(Reader reader) throws Exception;
}
