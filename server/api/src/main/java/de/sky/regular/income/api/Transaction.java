package de.sky.regular.income.api;

import de.sky.regular.income.api.detail.MetaInformation;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Transaction extends MetaInformation {
    public Integer amountInCents;

    public LocalDate date;
    public Integer dateRank;

    public Boolean isPeriodic;

    public List<String> reasons;
}