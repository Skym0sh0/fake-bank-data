/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income;


import generated.sky.regular.income.tables.BankStatement;
import generated.sky.regular.income.tables.FinancialTransaction;
import generated.sky.regular.income.tables.records.BankStatementRecord;
import generated.sky.regular.income.tables.records.FinancialTransactionRecord;

import javax.annotation.Generated;

import org.jooq.ForeignKey;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>REGULAR_INCOME</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<BankStatementRecord> BANK_STATEMENT_PKEY = UniqueKeys0.BANK_STATEMENT_PKEY;
    public static final UniqueKey<FinancialTransactionRecord> FINANCIAL_TRANSACTION_PKEY = UniqueKeys0.FINANCIAL_TRANSACTION_PKEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<FinancialTransactionRecord, BankStatementRecord> FINANCIAL_TRANSACTION__FINANCIAL_TRANSACTION_BANK_STATEMENT_ID_FKEY = ForeignKeys0.FINANCIAL_TRANSACTION__FINANCIAL_TRANSACTION_BANK_STATEMENT_ID_FKEY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class UniqueKeys0 {
        public static final UniqueKey<BankStatementRecord> BANK_STATEMENT_PKEY = Internal.createUniqueKey(BankStatement.BANK_STATEMENT, "bank_statement_pkey", BankStatement.BANK_STATEMENT.ID);
        public static final UniqueKey<FinancialTransactionRecord> FINANCIAL_TRANSACTION_PKEY = Internal.createUniqueKey(FinancialTransaction.FINANCIAL_TRANSACTION, "financial_transaction_pkey", FinancialTransaction.FINANCIAL_TRANSACTION.ID);
    }

    private static class ForeignKeys0 {
        public static final ForeignKey<FinancialTransactionRecord, BankStatementRecord> FINANCIAL_TRANSACTION__FINANCIAL_TRANSACTION_BANK_STATEMENT_ID_FKEY = Internal.createForeignKey(generated.sky.regular.income.Keys.BANK_STATEMENT_PKEY, FinancialTransaction.FINANCIAL_TRANSACTION, "financial_transaction__financial_transaction_bank_statement_id_fkey", FinancialTransaction.FINANCIAL_TRANSACTION.BANK_STATEMENT_ID);
    }
}