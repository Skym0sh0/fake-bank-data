package de.sky.regular.income.importing.csv.parsers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DKBTurnoverCsvParserTest {
    private static final String EXAMPLE_RAW_DATA = """
            "Kontonummer:";"DE121203345456567567 / Sparkonto";
                        
            "Von:";"01.07.2022";
            "Bis:";"30.12.2023";
            "Kontostand vom 30.12.2022:";"1.989,43 EUR";
            "Freitextsuche (im Verwendungszweck und Empfängerdaten):";"";"im Verwendungszweck und Empfängerdaten";
            "Betrag von:";"";"bis:";"";
                        
            "Buchungstag";"Wertstellung";"Buchungstext";"Auftraggeber / Begünstigter";"Verwendungszweck";"Kontonummer";"BLZ";"Betrag (EUR)";"Gläubiger-ID";"Mandatsreferenz";"Kundenreferenz";
            "29.12.2023";"01.01.2024";"Abschluss";"";"Abrechnung 29.12.2023siehe AnlageAbrechnung 29.12.2023Information zur AbrechnungKontostand am 29.12.2023                                       12.989,43 +Abrechnungszeitraum vom 01.10.2023 bis 31.12.2023Abrechnung 31.12.2023                                                0,00+Sollzinssätze am 30.12.2023 9,9000 v.H. für eingeräumte Kontoüberziehung(aktuell eingeräumte Kontoüberziehung         500,00) 9,9000 v.H. für geduldete Kontoüberziehungüber die eingeräumte Kontoüberziehung hinausKontostand/Rechnungsabschluss am 29.12.2022                    12.989,43 +Rechnungsnummer: 24435345-BY111-04353455640";"1345345345";"32432400";"0,00";"";"";"";
            "06.12.2023";"07.12.2023";"Wertpapiere";"";"Depot 0564561785           Wertp.Abrechn. 05.12.2023  000006567657670 WKN A0RPWJ Gesch.Art KV                                          ISHSIII-MSCI EM USD(ACC)   ISIN IE00B3546C18                                     Ihr Fondssparplan          Preis       31,73190000 EURStück           9,4542";"0";"0";"-301,50";"";"";"";
            "06.12.2023";"07.12.2023";"Wertpapiere";"";"Depot 0456854645           Wertp.Abrechn. 05.12.2023  000006657546100 WKN A2PM52 Gesch.Art KV                                          L+G-L+G CLEAN WATER U.ETF  ISIN IE036546C891                                     Ihr Fondssparplan          Preis       14,53400000 EURStück           3,4402";"0";"0";"-51,50";"";"";"";
            "06.12.2023";"07.12.2023";"Wertpapiere";"";"Depot 0542665765           Wertp.Abrechn. 05.12.2023  000065346763750 WKN A0RPWH Gesch.Art KV                                          ISHSIII-CORE MSCI WLD DLA  ISIN IE0456456483                                     Ihr Fondssparplan          Preis       79,70391180 EURStück           8,7825";"0";"0";"-701,50";"";"";"";
            "05.01.2024";"05.01.2024";"Kartenzahlung";"REWE Laden oHG";"2024-01-04      Debitk.46 VISA Debit";"DE95624760706778267804";"BYLADEM1001";"-18,21";"";"";"445345425524512";
            "03.01.2024";"03.01.2024";"Überweisung";"Peter Mueller";"Monatliche Ausgaben";"DE25345345435464565640";"BYLADEM1001";"-1.000,00";"";"";"";
            "02.01.2024";"02.01.2024";"Dauerauftrag";"Heinz Meier";"Miete + Nebenkosten Altschauerberg 8";"DE53454565676868678976";"GENODE35BBB";"-1.435,00";"";"";"NOTPROVIDED";
            "02.01.2024";"02.01.2024";"Kartenzahlung";"REWE Laden oHG";"2023-12-29      Debitk.18 VISA Debit";"DE36537657468768678904";"BYLADEM1001";"-61,66";"";"";"482343256654605";
            """;

    private final DKBTurnoverCsvParser parser = new DKBTurnoverCsvParser();

    @Test
    void checkThatParsingWorks() throws Exception {
        var result = parser.parseCsv(EXAMPLE_RAW_DATA);

        assertThat(result)
                .isNotNull()
                .hasSize(7);
    }
}
