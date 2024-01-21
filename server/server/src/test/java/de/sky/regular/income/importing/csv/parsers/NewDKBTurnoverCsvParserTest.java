package de.sky.regular.income.importing.csv.parsers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.StringReader;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class NewDKBTurnoverCsvParserTest {
    private static final String EXAMPLE_RAW_DATA = """
                                
                ""
                
                
                "Kontostand vom 20.01.2024:";"124,60 EUR"
                ""
                
                
                "Buchungsdatum";"Wertstellung";"Status";"Zahlungspflichtige*r";"Zahlungsempfänger*in";"Verwendungszweck";"Umsatztyp";"IBAN";"Betrag (€)";"Gläubiger-ID";"Mandatsreferenz";"Kundenreferenz"
                "22.01.24";"";"Vorgemerkt";"ISSUER";"Netto Marken-Discount    Blaubach    DE";"2024-01-20T08:53 Debitk. 0 2099-12 Zahl.System VISA De bit     (POS)";"Ausgang";"DE15651651561266347791";"-7,02";"";"";"484254466542865"
                "22.01.24";"";"Vorgemerkt";"ISSUER";"ALDI SUED                Niederkassel DE";"2024-01-19T18:33 Debitk. 0 2099-12 Zahl.System VISA De bit     (POS)";"Ausgang";"DE86429863426842634691";"-33,07";"";"";"484546484534581"
                "18.01.24";"18.01.24";"Gebucht";"Peter Müller";"Peter Müller + Peter Tester";"Sylvester Kosten";"Eingang";"DE83145645645232783541";"37";"";"";""
                """;

    private final NewDKBTurnoverCsvParser parser = new NewDKBTurnoverCsvParser();

    @Test
    void checkThatParsingWorks() throws Exception {
        var result = parser.parseCsv(new StringReader(EXAMPLE_RAW_DATA));

        assertThat(result)
                .isNotNull()
                .hasSize(3);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8})
    void whenHeaderIsDifferent(int skipFirstLines) throws Exception {
        var data = EXAMPLE_RAW_DATA.lines()
                .skip(skipFirstLines)
                .collect(Collectors.joining("\n"));

        var result = parser.parseCsv(new StringReader(data));

        assertThat(result)
                .isNotNull()
                .hasSize(3);
    }
}
