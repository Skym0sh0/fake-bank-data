package de.sky.regular.income.importing.csv.parsers;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class TurnoverRecord {
    LocalDate date;
    Integer amountInCents;

    String description;
    String suggestedCategory;
    String recipient;

    Long lineNumber;
    List<TurnoverRawRecordValues> rawValues;

    public record TurnoverRawRecordValues(String column, String value) {
    }
}
