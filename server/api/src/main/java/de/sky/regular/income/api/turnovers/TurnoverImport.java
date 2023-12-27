package de.sky.regular.income.api.turnovers;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TurnoverImport {
    public UUID id;
    public LocalDate date;
    public List<TurnoverRow> turnovers;
}
