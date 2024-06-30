package de.sky.regular.income.api.turnovers;

import lombok.Data;

import java.util.List;

@Data
public class TurnoverImportPatch {
    public TurnoverImportFormat format;
    public String encoding;
    public List<TurnoverRow> rows;
}
