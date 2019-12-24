package de.sky.regular.income.api;

import de.sky.regular.income.api.detail.PatchInformation;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class StatementPatch extends PatchInformation {
    public LocalDate date;
    public Integer finalBalanceInCents;

    public UUID previousStatementId;
    public Integer previousBalanceInCents;

    public List<TransactionPatch> transactions;
}
