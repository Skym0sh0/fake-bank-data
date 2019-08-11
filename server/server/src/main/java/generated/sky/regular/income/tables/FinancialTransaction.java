/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables;


import generated.sky.regular.income.Indexes;
import generated.sky.regular.income.Keys;
import generated.sky.regular.income.RegularIncome;
import generated.sky.regular.income.tables.records.FinancialTransactionRecord;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class FinancialTransaction extends TableImpl<FinancialTransactionRecord> {

    private static final long serialVersionUID = -1142623390;

    /**
     * The reference instance of <code>REGULAR_INCOME.financial_transaction</code>
     */
    public static final FinancialTransaction FINANCIAL_TRANSACTION = new FinancialTransaction();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FinancialTransactionRecord> getRecordType() {
        return FinancialTransactionRecord.class;
    }

    /**
     * The column <code>REGULAR_INCOME.financial_transaction.id</code>.
     */
    public final TableField<FinancialTransactionRecord, UUID> ID = createField("id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.financial_transaction.bank_statement_id</code>.
     */
    public final TableField<FinancialTransactionRecord, UUID> BANK_STATEMENT_ID = createField("bank_statement_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.financial_transaction.date_record</code>.
     */
    public final TableField<FinancialTransactionRecord, Date> DATE_RECORD = createField("date_record", org.jooq.impl.SQLDataType.DATE.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.financial_transaction.amount_value_cents</code>.
     */
    public final TableField<FinancialTransactionRecord, Integer> AMOUNT_VALUE_CENTS = createField("amount_value_cents", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.financial_transaction.is_periodic</code>.
     */
    public final TableField<FinancialTransactionRecord, Boolean> IS_PERIODIC = createField("is_periodic", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>REGULAR_INCOME.financial_transaction.reason</code>.
     */
    public final TableField<FinancialTransactionRecord, String> REASON = createField("reason", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.financial_transaction.checksum</code>.
     */
    public final TableField<FinancialTransactionRecord, String> CHECKSUM = createField("checksum", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>REGULAR_INCOME.financial_transaction</code> table reference
     */
    public FinancialTransaction() {
        this(DSL.name("financial_transaction"), null);
    }

    /**
     * Create an aliased <code>REGULAR_INCOME.financial_transaction</code> table reference
     */
    public FinancialTransaction(String alias) {
        this(DSL.name(alias), FINANCIAL_TRANSACTION);
    }

    /**
     * Create an aliased <code>REGULAR_INCOME.financial_transaction</code> table reference
     */
    public FinancialTransaction(Name alias) {
        this(alias, FINANCIAL_TRANSACTION);
    }

    private FinancialTransaction(Name alias, Table<FinancialTransactionRecord> aliased) {
        this(alias, aliased, null);
    }

    private FinancialTransaction(Name alias, Table<FinancialTransactionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> FinancialTransaction(Table<O> child, ForeignKey<O, FinancialTransactionRecord> key) {
        super(child, key, FINANCIAL_TRANSACTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return RegularIncome.REGULAR_INCOME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.FINANCIAL_TRANSACTION_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<FinancialTransactionRecord> getPrimaryKey() {
        return Keys.FINANCIAL_TRANSACTION_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<FinancialTransactionRecord>> getKeys() {
        return Arrays.<UniqueKey<FinancialTransactionRecord>>asList(Keys.FINANCIAL_TRANSACTION_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<FinancialTransactionRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<FinancialTransactionRecord, ?>>asList(Keys.FINANCIAL_TRANSACTION__FINANCIAL_TRANSACTION_BANK_STATEMENT_ID_FKEY);
    }

    public BankStatement bankStatement() {
        return new BankStatement(this, Keys.FINANCIAL_TRANSACTION__FINANCIAL_TRANSACTION_BANK_STATEMENT_ID_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FinancialTransaction as(String alias) {
        return new FinancialTransaction(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FinancialTransaction as(Name alias) {
        return new FinancialTransaction(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public FinancialTransaction rename(String name) {
        return new FinancialTransaction(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public FinancialTransaction rename(Name name) {
        return new FinancialTransaction(name, null);
    }
}
