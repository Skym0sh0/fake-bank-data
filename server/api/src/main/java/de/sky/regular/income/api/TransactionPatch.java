package de.sky.regular.income.api;

import de.sky.regular.income.api.detail.PatchInformation;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class TransactionPatch extends PatchInformation {
    public Integer amountInCents;

    public LocalDate date;
    public Integer dateRank;

    public Boolean isPeriodic;

    public UUID categoryId;
}
