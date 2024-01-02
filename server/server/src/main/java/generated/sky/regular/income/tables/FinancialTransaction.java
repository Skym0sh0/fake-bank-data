/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables;


import generated.sky.regular.income.Indexes;
import generated.sky.regular.income.Keys;
import generated.sky.regular.income.RegularIncome;
import generated.sky.regular.income.tables.records.FinancialTransactionRecord;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.jooq.Check;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row9;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class FinancialTransaction extends TableImpl<FinancialTransactionRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of
     * <code>REGULAR_INCOME.financial_transaction</code>
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
    public final TableField<FinancialTransactionRecord, UUID> ID = createField(DSL.name("id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column
     * <code>REGULAR_INCOME.financial_transaction.bank_statement_id</code>.
     */
    public final TableField<FinancialTransactionRecord, UUID> BANK_STATEMENT_ID = createField(DSL.name("bank_statement_id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.financial_transaction.date_record</code>.
     */
    public final TableField<FinancialTransactionRecord, LocalDate> DATE_RECORD = createField(DSL.name("date_record"), SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column
     * <code>REGULAR_INCOME.financial_transaction.amount_value_cents</code>.
     */
    public final TableField<FinancialTransactionRecord, Integer> AMOUNT_VALUE_CENTS = createField(DSL.name("amount_value_cents"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.financial_transaction.is_periodic</code>.
     */
    public final TableField<FinancialTransactionRecord, Boolean> IS_PERIODIC = createField(DSL.name("is_periodic"), SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.financial_transaction.reason</code>.
     */
    public final TableField<FinancialTransactionRecord, String> REASON = createField(DSL.name("reason"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>REGULAR_INCOME.financial_transaction.checksum</code>.
     */
    public final TableField<FinancialTransactionRecord, String> CHECKSUM = createField(DSL.name("checksum"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.financial_transaction.created_at</code>.
     */
    public final TableField<FinancialTransactionRecord, OffsetDateTime> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false).defaultValue(DSL.field("now()", SQLDataType.TIMESTAMPWITHTIMEZONE)), this, "");

    /**
     * The column <code>REGULAR_INCOME.financial_transaction.category_id</code>.
     */
    public final TableField<FinancialTransactionRecord, UUID> CATEGORY_ID = createField(DSL.name("category_id"), SQLDataType.UUID.nullable(false), this, "");

    private FinancialTransaction(Name alias, Table<FinancialTransactionRecord> aliased) {
        this(alias, aliased, null);
    }

    private FinancialTransaction(Name alias, Table<FinancialTransactionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>REGULAR_INCOME.financial_transaction</code> table
     * reference
     */
    public FinancialTransaction(String alias) {
        this(DSL.name(alias), FINANCIAL_TRANSACTION);
    }

    /**
     * Create an aliased <code>REGULAR_INCOME.financial_transaction</code> table
     * reference
     */
    public FinancialTransaction(Name alias) {
        this(alias, FINANCIAL_TRANSACTION);
    }

    /**
     * Create a <code>REGULAR_INCOME.financial_transaction</code> table
     * reference
     */
    public FinancialTransaction() {
        this(DSL.name("financial_transaction"), null);
    }

    public <O extends Record> FinancialTransaction(Table<O> child, ForeignKey<O, FinancialTransactionRecord> key) {
        super(child, key, FINANCIAL_TRANSACTION);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : RegularIncome.REGULAR_INCOME;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.IDX_FK_TRANSACTION_X_STATEMENT);
    }

    @Override
    public UniqueKey<FinancialTransactionRecord> getPrimaryKey() {
        return Keys.FINANCIAL_TRANSACTION_PKEY;
    }

    @Override
    public List<ForeignKey<FinancialTransactionRecord, ?>> getReferences() {
        return Arrays.asList(Keys.FINANCIAL_TRANSACTION__FINANCIAL_TRANSACTION_BANK_STATEMENT_ID_FKEY, Keys.FINANCIAL_TRANSACTION__FINANCIAL_TRANSACTION_CATEGORY_ID_FKEY);
    }

    private transient BankStatement _bankStatement;
    private transient Category _category;

    /**
     * Get the implicit join path to the <code>public.bank_statement</code>
     * table.
     */
    public BankStatement bankStatement() {
        if (_bankStatement == null)
            _bankStatement = new BankStatement(this, Keys.FINANCIAL_TRANSACTION__FINANCIAL_TRANSACTION_BANK_STATEMENT_ID_FKEY);

        return _bankStatement;
    }

    /**
     * Get the implicit join path to the <code>public.category</code> table.
     */
    public Category category() {
        if (_category == null)
            _category = new Category(this, Keys.FINANCIAL_TRANSACTION__FINANCIAL_TRANSACTION_CATEGORY_ID_FKEY);

        return _category;
    }

    @Override
    public List<Check<FinancialTransactionRecord>> getChecks() {
        return Arrays.asList(
            Internal.createCheck(this, DSL.name("transaction_amount_not_zero"), "((amount_value_cents <> 0))", true)
        );
    }

    @Override
    public FinancialTransaction as(String alias) {
        return new FinancialTransaction(DSL.name(alias), this);
    }

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

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<UUID, UUID, LocalDate, Integer, Boolean, String, String, OffsetDateTime, UUID> fieldsRow() {
        return (Row9) super.fieldsRow();
    }
}
