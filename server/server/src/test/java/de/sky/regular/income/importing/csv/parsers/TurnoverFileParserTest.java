package de.sky.regular.income.importing.csv.parsers;

import de.sky.regular.income.api.turnovers.RawCsvTable;
import de.sky.regular.income.importing.csv.TurnoverFileParser;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.StringReader;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

class TurnoverFileParserTest {
    private final TurnoverFileParser parser = new TurnoverFileParser(
            new DKBTurnoverCsvParser(),
            new VRBankTurnoverCsvParser(),
            new PaypalTurnoverParser()
    );

    @ParameterizedTest
    @ValueSource(strings = {";", "|", ","})
    void checkBasicParsing(String sep) {
        Arrays.asList("", "\"")
                .forEach(quote -> {
                    var header = IntStream.rangeClosed('a', 'e')
                            .mapToObj(c -> String.valueOf((char) c))
                            .collect(Collectors.joining(quote + sep + quote, quote, quote));
                    var data = IntStream.rangeClosed(1, 5)
                            .mapToObj(String::valueOf)
                            .collect(Collectors.joining(quote + sep + quote, quote, quote));

                    var csv = header + "\n" + data;

                    var result = parser.parseRawCsv(new StringReader(csv));

                    assertThat(result)
                            .as("CSV is: %n%s", csv)
                            .returns(2, RawCsvTable::getRows)
                            .returns(5, RawCsvTable::getColumns)
                            .extracting(RawCsvTable::getData, as(list(String[].class)))
                            .containsExactly(
                                    new String[]{"a", "b", "c", "d", "e"},
                                    new String[]{"1", "2", "3", "4", "5"}
                            );
                });
    }

    @Nested
    class WithUnregularCsv {
        private final String csv = """
                x|x
                                
                a|b|c|d|e
                1|2
                1|2|3|4|5|6|7
                                
                                
                """;

        @Test
        void checkUnregularCsv() {
            var result = parser.parseRawCsv(new StringReader(csv));

            assertThat(result)
                    .as("CSV is: %n%s", csv)
                    .returns(4, RawCsvTable::getRows)
                    .returns(7, RawCsvTable::getColumns)
                    .extracting(RawCsvTable::getData, as(list(String[].class)))
                    .containsExactly(
                            new String[]{"x", "x", null, null, null, null, null},
                            new String[]{"a", "b", "c", "d", "e", null, null},
                            new String[]{"1", "2", null, null, null, null, null},
                            new String[]{"1", "2", "3", "4", "5", "6", "7"}
                    )
                    .doesNotContain(new String[]{});
        }

        @Test
        void printAsTable() {
            var table = parser.parseRawCsv(new StringReader(csv));

            System.out.println("Rows: " + table.getRows() + " Columns: " + table.getColumns());
            int length = 5;

            table.getData()
                    .stream()
                    .map(row -> {
                        return Arrays.stream(row)
                                .map(cell -> cell != null ? cell : "")
                                .map(cell -> StringUtils.abbreviate(cell, length))
                                .map(cell -> cell + StringUtils.repeat(' ', length))
                                .map(cell -> cell.substring(0, length))
                                .collect(Collectors.joining(" | ", "| ", " |"));
                    })
                    .forEach(System.out::println);
        }
    }
}
