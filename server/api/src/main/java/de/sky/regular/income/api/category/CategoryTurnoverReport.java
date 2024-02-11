package de.sky.regular.income.api.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CategoryTurnoverReport {
    public UUID categoryId;

    public List<ReportDatapoint> datapoints;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReportDatapoint {
        public UUID categoryId;
        public LocalDate date;
        public Integer incomeAmountInCents;
        public Integer expenseAmountInCents;
    }
}
