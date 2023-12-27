package de.sky.regular.income.api.turnovers;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class TurnoverImportPatch {
    public List<TurnoverRow> rows;
}
