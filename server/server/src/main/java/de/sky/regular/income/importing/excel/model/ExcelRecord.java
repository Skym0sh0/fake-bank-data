package de.sky.regular.income.importing.excel.model;

import lombok.Data;

@Data
public class ExcelRecord {
    public Boolean isStatement;

    public ExcelBankStatementRecord statement;
    public ExcelTransactionRecord transaction;
}
