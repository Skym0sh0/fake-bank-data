package de.sky.regular.income.api.turnovers;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;

import java.time.LocalDate;
import java.util.UUID;

@Data
@SuperBuilder
public class TurnoverRow {
    public UUID id;
    public LocalDate date;
    public Integer amountInCents;
    public UUID categoryId;
    public String checksum;
    public String similarityChecksum;
    public String description;
    public String suggestedCategory;
    public String recipient;

    @Tolerate
    public TurnoverRow() {
    }
}
