package de.sky.regular.income.api.turnovers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoverImportRowsPatch {
    public List<RowPatch> rows;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RowPatch {
        public UUID id;
        public UUID categoryId;
    }
}
