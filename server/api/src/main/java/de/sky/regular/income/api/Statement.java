package de.sky.regular.income.api;

import de.sky.regular.income.api.detail.UpdatedMetaInformation;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Statement extends UpdatedMetaInformation {
    public LocalDate date;
    public Integer balanceInCents;

    public Statement previousStatement;

    public List<Transaction> transactions;
}
