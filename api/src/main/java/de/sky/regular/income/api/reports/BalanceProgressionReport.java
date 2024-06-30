package de.sky.regular.income.api.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceProgressionReport {

    public List<DataPoint> data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataPoint {
        public Integer index;
        public LocalDate date;
        public Integer perDayBalanceInCents;
        public Integer balanceInCents;
    }
}
