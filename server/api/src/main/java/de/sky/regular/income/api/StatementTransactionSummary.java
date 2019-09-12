package de.sky.regular.income.api;

import lombok.Data;

import java.util.UUID;

@Data
public class StatementTransactionSummary {
    public UUID statementId;

    public Summary income;
    public Summary expense;

    public Summary total;

    @Data
    public static class Summary {
        public Integer count;
        public Integer total;
        public Integer average;
        public Integer median;
        public Integer min;
        public Integer max;
    }
}
