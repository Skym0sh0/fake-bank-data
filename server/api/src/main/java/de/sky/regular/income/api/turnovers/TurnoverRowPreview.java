package de.sky.regular.income.api.turnovers;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class TurnoverRowPreview extends TurnoverRow {
    public Boolean importable;
    public List<CategorySuggestion> suggestedCategories;

    @Data
    public static class CategorySuggestion {
        public UUID categoryId;

        public int frequency;
    }
}
