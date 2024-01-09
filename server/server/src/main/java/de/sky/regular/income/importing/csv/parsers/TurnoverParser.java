package de.sky.regular.income.importing.csv.parsers;

import de.sky.regular.income.api.turnovers.TurnoverImportFormat;

import java.io.InputStream;
import java.util.List;

public interface TurnoverParser {
    TurnoverImportFormat getSupportedFormat();

    List<TurnoverRecord> parseCsv(InputStream is) throws Exception;
}
