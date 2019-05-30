package de.sky.regular.income.api;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class Transaction {
    public UUID id;
    public Long amountInCent;
    public LocalDate timestamp;
    public Boolean isPeriodic;
    public String reason;
}