package de.sky.regular.income;

import de.sky.regular.income.api.TurnoverImportFormat;
import generated.sky.regular.income.tables.TurnoverFileImport;
import org.jooq.Check;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseConstraintCheckTest {
    @ParameterizedTest
    @EnumSource(TurnoverImportFormat.class)
    void checkThatCheckConstraintContainsAllFormats(TurnoverImportFormat frmt) {
        assertThat(TurnoverFileImport.TURNOVER_FILE_IMPORT.getChecks())
                .first()
                .returns("TURNOVER_FILE_IMPORT_TURNOVER_FILE_FORMAT_CHECK", s -> s.getName().toUpperCase())
                .extracting(Check::condition)
                .asString()
                .contains("'%s'".formatted(frmt.name()));
    }
}
