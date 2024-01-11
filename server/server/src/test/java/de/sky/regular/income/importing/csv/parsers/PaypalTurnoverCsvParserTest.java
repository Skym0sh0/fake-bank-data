package de.sky.regular.income.importing.csv.parsers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PaypalTurnoverCsvParserTest {
    private static final String EXAMPLE_RAW_DATA = """
            "Datum","Uhrzeit","Zeitzone","Beschreibung","Währung","Brutto","Entgelt","Netto","Guthaben","Transaktionscode","Absender E-Mail-Adresse","Name","Name der Bank","Bankkonto","Versand- und Bearbeitungsgebühr","Umsatzsteuer","Rechnungsnummer","Zugehöriger Transaktionscode"
            "01.07.2023","12:21:25","Europe/Berlin","Allgemeine Zahlung","EUR","-360,00","0,00","-360,00","-360,00","745JFGJKDFG455H5A","peter.muffti@gmail.com","Peter Muffensausen","","","0,00","0,00","",""
            "01.07.2023","12:21:25","Europe/Berlin","Bankgutschrift auf PayPal-Konto","EUR","360,00","0,00","360,00","0,00","FDGJK564M4456456C","","","SowasBank wie Direkt e.G.","0011","0,00","0,00","","745JFGJKDFG455H5A"
            "13.07.2023","11:34:43","Europe/Berlin","Allgemeine Zahlung","EUR","12,00","0,00","12,00","12,00","KASFHJDFJH8735836","typ@googlemail.com","1 Typ","","","0,00","0,00","",""
            "14.07.2023","10:17:18","Europe/Berlin","Zahlung im Einzugsverfahren mit Zahlungsrechnung","EUR","-35,99","0,00","-35,99","-23,99","HAF78875DSFKJDSF8","some-email@address.com","www.address.com","","","0,00","0,00","78445HFMN9834JHDF45","B-JHFJDFDF874593DFMFDSMSDFM3674DMNF"
            "14.07.2023","10:17:18","Europe/Berlin","Bankgutschrift auf PayPal-Konto","EUR","23,99","0,00","23,99","0,00","7HM293731E6649907","","","VR-Bank Bonn Rhein-Sieg eG","0011","0,00","0,00","7324783284723874832","7A6324G87G5763Z45X7884"
            """;

    private final PaypalTurnoverParser parser = new PaypalTurnoverParser();

    @Test
    void checkThatParsingWorks() throws Exception {
        var result = parser.parseCsv(EXAMPLE_RAW_DATA);

        assertThat(result)
                .isNotNull()
                .hasSize(5);
    }
}
