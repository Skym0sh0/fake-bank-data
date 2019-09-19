package de.sky.regular.income.importing.excel.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExcelBankStatementRecord {
    public LocalDate date;
    public double balance;
}
