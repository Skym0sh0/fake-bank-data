package de.sky.regular.income.api;

import lombok.Data;

import java.util.UUID;

@Data
public class StatementTransactionSummary {
    public UUID statementId;

    public StatisticalSummary income;
    public StatisticalSummary expense;

    public StatisticalSummary total;
}
