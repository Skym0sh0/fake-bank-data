/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income;


import generated.sky.regular.income.tables.BackupHistory;
import generated.sky.regular.income.tables.BankStatement;
import generated.sky.regular.income.tables.Category;
import generated.sky.regular.income.tables.FinancialTransaction;
import generated.sky.regular.income.tables.VOrderedBankStatements;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in REGULAR_INCOME
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.12"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>REGULAR_INCOME.backup_history</code>.
     */
    public static final BackupHistory BACKUP_HISTORY = generated.sky.regular.income.tables.BackupHistory.BACKUP_HISTORY;

    /**
     * The table <code>REGULAR_INCOME.bank_statement</code>.
     */
    public static final BankStatement BANK_STATEMENT = generated.sky.regular.income.tables.BankStatement.BANK_STATEMENT;

    /**
     * The table <code>REGULAR_INCOME.category</code>.
     */
    public static final Category CATEGORY = generated.sky.regular.income.tables.Category.CATEGORY;

    /**
     * The table <code>REGULAR_INCOME.financial_transaction</code>.
     */
    public static final FinancialTransaction FINANCIAL_TRANSACTION = generated.sky.regular.income.tables.FinancialTransaction.FINANCIAL_TRANSACTION;

    /**
     * The table <code>REGULAR_INCOME.v_ordered_bank_statements</code>.
     */
    public static final VOrderedBankStatements V_ORDERED_BANK_STATEMENTS = generated.sky.regular.income.tables.VOrderedBankStatements.V_ORDERED_BANK_STATEMENTS;
}
