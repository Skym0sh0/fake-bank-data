package de.sky.regular.income.importing.csv.parsers;

import de.sky.regular.income.api.turnovers.TurnoverImportFormat;

import java.io.InputStream;
import java.util.List;

public class PaypalTurnoverParser implements TurnoverParser {
    @Override
    public TurnoverImportFormat getSupportedFormat() {
        return TurnoverImportFormat.PAYPAL;
    }

    @Override
    public List<TurnoverRecord> parseCsv(InputStream is) throws Exception {
        return List.of();
    }
}
