package de.sky.regular.income.api.turnovers;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class TurnoverRowPreview extends TurnoverRow {
    public Boolean importable;
}
