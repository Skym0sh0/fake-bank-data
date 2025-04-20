package de.sky.regular.income.importing.csv.parsers;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Map;

@Value
@Builder
public class TurnoverRecord {
    LocalDate date;
    Integer amountInCents;

    String description;
    String suggestedCategory;
    String recipient;

    Long lineNumber;
    Map<String, String> rawValuePairs;
}
