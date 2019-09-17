package de.sky.regular.income.api;

import de.sky.regular.income.api.detail.MetaInformation;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class Statement extends MetaInformation {
    public LocalDate date;
    public Integer balanceInCents;

    public Statement previousStatement;

    public List<Transaction> transactions;
}
