package de.sky.regular.income.api.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicCoarseInfo {
    public UUID userId;

    public LocalDate earliestTransaction;
    public LocalDate latestTransaction;

    public Integer numberOfTransactions;
    public Integer numberOfUsedCategories;
    public Integer maxDepthOfCategories;
}
