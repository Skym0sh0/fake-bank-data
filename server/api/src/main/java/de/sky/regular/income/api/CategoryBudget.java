package de.sky.regular.income.api;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class CategoryBudget {
    public Integer budgetInCents;
    public Double exceedingThreshold;
}
