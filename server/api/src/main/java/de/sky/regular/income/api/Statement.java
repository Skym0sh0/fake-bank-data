package de.sky.regular.income.api;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class Statement {
    public UUID id;
    public LocalDate date;
    public Long balance;
}
