package de.sky.regular.income.api;

import lombok.Data;

@Data
public class StatisticalSummary {
    public Integer count;
    public Integer total;
    public Integer average;
    public Integer median;
    public Integer min;
    public Integer max;
}
