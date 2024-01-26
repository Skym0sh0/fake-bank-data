package de.sky.regular.income.api.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
        public String fromCategory;
        public String toCategory;
        public Integer amountInCents;
        public int depthLevel;

        public FlowDataPoint(String fromCategory, String toCategory, Integer amountInCents) {
            this(fromCategory, toCategory, amountInCents, 0);
        }
    }
}
