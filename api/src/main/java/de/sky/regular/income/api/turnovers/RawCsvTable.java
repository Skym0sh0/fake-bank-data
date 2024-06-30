package de.sky.regular.income.api.turnovers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawCsvTable {
    public int rows;
    public int columns;

    public List<String[]> data;
}
