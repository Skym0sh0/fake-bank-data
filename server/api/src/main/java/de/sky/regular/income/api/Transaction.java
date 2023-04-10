package de.sky.regular.income.api;

import de.sky.regular.income.api.detail.CreatedMetaInformation;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class Transaction extends CreatedMetaInformation {
    public Integer amountInCents;

    public LocalDate date;
    public Integer dateRank;

    public Boolean isPeriodic;

    public UUID categoryId;
}