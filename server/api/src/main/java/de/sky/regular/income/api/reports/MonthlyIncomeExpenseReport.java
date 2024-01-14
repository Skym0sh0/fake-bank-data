package de.sky.regular.income.api.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyIncomeExpenseReport {
    public List<DataPoint> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPoint {
        public LocalDate month;
        public Integer incomeInCents;
        public Integer expenseInCents;
    }
}
