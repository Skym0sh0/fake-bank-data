package de.sky.regular.income.api.turnovers;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class TurnoverRow {
    public LocalDate date;
    public Integer amountInCents;
    public UUID categoryId;
    public String checksum;
    public String description;
    public String suggestedCategory;
    public String recipient;

    @Tolerate
    public TurnoverRow() {
    }
}
