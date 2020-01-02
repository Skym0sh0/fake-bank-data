package de.sky.regular.income.api.reports;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MonthlyIncomeExpenseReport {
    public List<DataPoint> data;

    @Data
    public static class DataPoint {
        public LocalDate month;
        public Integer incomeInCents;
        public Integer expenseInCents;
    }
}
