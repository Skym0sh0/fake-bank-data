/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income;


import generated.sky.regular.income.tables.BackupHistory;
import generated.sky.regular.income.tables.BankStatement;
import generated.sky.regular.income.tables.Category;
import generated.sky.regular.income.tables.FinancialTransaction;

import javax.annotation.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables of the <code>REGULAR_INCOME</code> 
 * schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.12"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index BACKUP_HISTORY_PKEY = Indexes0.BACKUP_HISTORY_PKEY;
    public static final Index IDX_BACKUP_LAST_CHECK = Indexes0.IDX_BACKUP_LAST_CHECK;
    public static final Index BANK_STATEMENT_PKEY = Indexes0.BANK_STATEMENT_PKEY;
    public static final Index BANK_STATEMENT_PREVIOUS_STATEMENT_ID_KEY = Indexes0.BANK_STATEMENT_PREVIOUS_STATEMENT_ID_KEY;
    public static final Index CATEGORY_NAME_KEY = Indexes0.CATEGORY_NAME_KEY;
    public static final Index CATEGORY_PKEY = Indexes0.CATEGORY_PKEY;
    public static final Index FINANCIAL_TRANSACTION_PKEY = Indexes0.FINANCIAL_TRANSACTION_PKEY;
    public static final Index IDX_FK_TRANSACTION_X_STATEMENT = Indexes0.IDX_FK_TRANSACTION_X_STATEMENT;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index BACKUP_HISTORY_PKEY = Internal.createIndex("backup_history_pkey", BackupHistory.BACKUP_HISTORY, new OrderField[] { BackupHistory.BACKUP_HISTORY.ID }, true);
        public static Index IDX_BACKUP_LAST_CHECK = Internal.createIndex("idx_backup_last_check", BackupHistory.BACKUP_HISTORY, new OrderField[] { BackupHistory.BACKUP_HISTORY.LAST_CHECK }, true);
        public static Index BANK_STATEMENT_PKEY = Internal.createIndex("bank_statement_pkey", BankStatement.BANK_STATEMENT, new OrderField[] { BankStatement.BANK_STATEMENT.ID }, true);
        public static Index BANK_STATEMENT_PREVIOUS_STATEMENT_ID_KEY = Internal.createIndex("bank_statement_previous_statement_id_key", BankStatement.BANK_STATEMENT, new OrderField[] { BankStatement.BANK_STATEMENT.PREVIOUS_STATEMENT_ID }, true);
        public static Index CATEGORY_NAME_KEY = Internal.createIndex("category_name_key", Category.CATEGORY, new OrderField[] { Category.CATEGORY.NAME }, true);
        public static Index CATEGORY_PKEY = Internal.createIndex("category_pkey", Category.CATEGORY, new OrderField[] { Category.CATEGORY.ID }, true);
        public static Index FINANCIAL_TRANSACTION_PKEY = Internal.createIndex("financial_transaction_pkey", FinancialTransaction.FINANCIAL_TRANSACTION, new OrderField[] { FinancialTransaction.FINANCIAL_TRANSACTION.ID }, true);
        public static Index IDX_FK_TRANSACTION_X_STATEMENT = Internal.createIndex("idx_fk_transaction_x_statement", FinancialTransaction.FINANCIAL_TRANSACTION, new OrderField[] { FinancialTransaction.FINANCIAL_TRANSACTION.BANK_STATEMENT_ID }, false);
    }
}
