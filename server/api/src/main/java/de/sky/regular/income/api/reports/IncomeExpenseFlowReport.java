package de.sky.regular.income.api.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeExpenseFlowReport {
    public LocalDate start;
    public LocalDate end;

    public List<FlowDataPoint> flows;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FlowDataPoint {
        public UUID fromCategoryId;
        public UUID toCategoryId;
        public Integer amountInCents;
    }
}
