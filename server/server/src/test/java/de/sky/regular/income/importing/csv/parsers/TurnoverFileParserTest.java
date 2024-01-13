package de.sky.regular.income.importing.csv.parsers;

import de.sky.regular.income.api.turnovers.RawCsvTable;
import de.sky.regular.income.importing.csv.TurnoverFileParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
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

                    var result = parser.parseRawCsv(new ByteArrayInputStream(csv.getBytes()));

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

    @Test
    void checkUnregularCsv() {
        var csv = """
                x|x
                                
                a|b|c|d|e
                1|2
                1|2|3|4|5|6|7
                                
                                
                """;

        var result = parser.parseRawCsv(new ByteArrayInputStream(csv.getBytes()));

        assertThat(result)
                .as("CSV is: %n%s", csv)
                .returns(4, RawCsvTable::getRows)
                .returns(7, RawCsvTable::getColumns)
                .extracting(RawCsvTable::getData, as(list(String[].class)))
                .containsExactly(
                        new String[]{"x", "x"},
                        new String[]{"a", "b", "c", "d", "e"},
                        new String[]{"1", "2"},
                        new String[]{"1", "2", "3", "4", "5", "6", "7"}
                )
                .doesNotContain(new String[]{});
    }
}
