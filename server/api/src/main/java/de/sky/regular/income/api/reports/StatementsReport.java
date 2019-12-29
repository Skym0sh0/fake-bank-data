package de.sky.regular.income.api.reports;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class StatementsReport {

    public List<DataPoint> data;

    @Data
    public static class DataPoint {
        public LocalDate date;
        public Integer balanceInCents;
    }
}
