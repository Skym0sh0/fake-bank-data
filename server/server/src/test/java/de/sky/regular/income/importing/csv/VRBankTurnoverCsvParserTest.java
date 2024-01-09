package de.sky.regular.income.importing.csv;

import de.sky.regular.income.importing.csv.parsers.VRBankTurnoverCsvParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VRBankTurnoverCsvParserTest {
    private static final String EXAMPLE_RAW_DATA = "" +
            "Bezeichnung Auftragskonto;IBAN Auftragskonto;BIC Auftragskonto;Bankname Auftragskonto;Buchungstag;Valutadatum;Name Zahlungsbeteiligter;IBAN Zahlungsbeteiligter;BIC (SWIFT-Code) Zahlungsbeteiligter;Buchungstext;Verwendungszweck;Betrag;Waehrung;Saldo nach Buchung;Bemerkung;Kategorie;Steuerrelevant;Glaeubiger ID;Mandatsreferenz\n" +
            "VR-Direkt;DE453578912312381;GENODED1RST;VR-Bank eG;06.04.2023;06.04.2023;VR-BANK BONN RHEIN-SIEG;;;Auszahlung girocard;VR-BANK BONN RHEIN-SIEG    Neustadt/Teststadt/DE      06.04.2023/07:53 girocard  GA 1232323546/213543657/1235445345687645312/12326354587/0/671234;-100,00;EUR;8395,21;;Sonstiges;;;\n" +
            "VR-Direkt;DE452132130068018;GENODED1RST;VR-Bank eG;04.04.2023;04.04.2023;Mundorf;DE70300142560055000434;WELADEDDXXX;Kartenzahlung girocard;Mundorf Tank 43/Berlin-Tannenbusch/DE                 03.04.2023 um 19:35:13 Uhr 74069263/434485/CICC/FPIN  12234656/5698563265/0/1256 REF 512331/278947;-79,00;EUR;8495,21;;Sonstiges;;DE2134566842543472;514031\n" +
            "VR-Direkt;DE453125456875213;GENODED1RST;VR-Bank eG;04.04.2023;04.04.2023;REWE Laden oHG.;DE4130354542000148443610;WELADEDDXXX;Kartenzahlung girocard;REWE SAGT DANKE. 4365455351/Foostadt/DE                03.04.2023 um 19:46:43 Uhr 56021104/417183/ECTL/      37069756520/123654756898/0/7223;-23,42;EUR;8574,21;;Sonstiges;;DE5464531238819145;OFFLINE\n" +
            "";

    private final VRBankTurnoverCsvParser parser = new VRBankTurnoverCsvParser();

    @Test
    void checkThatParsingWorks() throws Exception {
        var result = parser.parseCsv(EXAMPLE_RAW_DATA);

        assertThat(result)
                .isNotNull()
                .hasSize(3);
    }
}
