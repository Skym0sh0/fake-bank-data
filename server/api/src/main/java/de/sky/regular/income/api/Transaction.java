package de.sky.regular.income.api;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class Transaction {
    public UUID id;
    public Integer amount;
    public LocalDate date;
    public Boolean isPeriodic;
    public String reason;
}