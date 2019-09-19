package de.sky.regular.income.importing.excel.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExcelTransactionRecord {
    public LocalDate date;
    public Double income;
    public Double expense;
    public String reason;
    public Boolean periodic;
}
