package de.sky.regular.income.importing.csv;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TurnoverCsvParserTest {
    private static final String EXAMPLE_RAW_DATA = "" +
            "Bezeichnung Auftragskonto;IBAN Auftragskonto;BIC Auftragskonto;Bankname Auftragskonto;Buchungstag;Valutadatum;Name Zahlungsbeteiligter;IBAN Zahlungsbeteiligter;BIC (SWIFT-Code) Zahlungsbeteiligter;Buchungstext;Verwendungszweck;Betrag;Waehrung;Saldo nach Buchung;Bemerkung;Kategorie;Steuerrelevant;Glaeubiger ID;Mandatsreferenz\n" +
            "VR-Direkt;DE453578912312381;GENODED1RST;VR-Bank Bonn Rhein-Sieg eG;06.04.2023;06.04.2023;VR-BANK BONN RHEIN-SIEG;;;Auszahlung girocard;VR-BANK BONN RHEIN-SIEG    Bergheim/Troisdorf/DE      06.04.2023/07:53 girocard  GA 12323546/21354357/12354687645312/1232354587/0/1234;-100,00;EUR;8395,21;;Sonstiges;;;\n" +
            "VR-Direkt;DE452132130068018;GENODED1RST;VR-Bank Bonn Rhein-Sieg eG;04.04.2023;04.04.2023;Mundorf;DE70300142560055000434;WELADEDDXXX;Kartenzahlung girocard;Mundorf Tank 43/Troisdorf-Bergheim/DE                 03.04.2023 um 19:35:13 Uhr 74069263/434485/CICC/FPIN  12234656/5698563265/0/1256 REF 512331/278947;-79,00;EUR;8495,21;;Sonstiges;;DE2134566842543472;514031\n" +
            "VR-Direkt;DE453125456875213;GENODED1RST;VR-Bank Bonn Rhein-Sieg eG;04.04.2023;04.04.2023;REWE Kelterbaum oHG.;DE4130354420001484310;WELADEDDXXX;Kartenzahlung girocard;REWE SAGT DANKE. 43655351/Troisdorf/DE                03.04.2023 um 19:46:43 Uhr 56021104/417183/ECTL/      37069520/1236547898/0/7223;-23,42;EUR;8574,21;;Sonstiges;;DE5464531238819145;OFFLINE\n" +
            "";

    private final TurnoverCsvParser parser = new TurnoverCsvParser();

    @Test
    void checkThatParsingWorks() throws Exception {
        var result = parser.parseCsv(EXAMPLE_RAW_DATA);

        assertThat(result)
                .isNotNull()
                .hasSize(3);
    }
}
