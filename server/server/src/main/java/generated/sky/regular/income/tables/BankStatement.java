/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables;


import generated.sky.regular.income.Indexes;
import generated.sky.regular.income.Keys;
import generated.sky.regular.income.RegularIncome;
import generated.sky.regular.income.tables.BankStatement.BankStatementPath;
import generated.sky.regular.income.tables.FinancialTransaction.FinancialTransactionPath;
import generated.sky.regular.income.tables.records.BankStatementRecord;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.Path;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BankStatement extends TableImpl<BankStatementRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>REGULAR_INCOME.bank_statement</code>
     */
    public static final BankStatement BANK_STATEMENT = new BankStatement();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BankStatementRecord> getRecordType() {
        return BankStatementRecord.class;
    }

    /**
     * The column <code>REGULAR_INCOME.bank_statement.id</code>.
     */
    public final TableField<BankStatementRecord, UUID> ID = createField(DSL.name("id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.bank_statement.date_record</code>.
     */
    public final TableField<BankStatementRecord, LocalDate> DATE_RECORD = createField(DSL.name("date_record"), SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column
     * <code>REGULAR_INCOME.bank_statement.amount_balance_cents</code>.
     */
    public final TableField<BankStatementRecord, Integer> AMOUNT_BALANCE_CENTS = createField(DSL.name("amount_balance_cents"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column
     * <code>REGULAR_INCOME.bank_statement.previous_statement_id</code>.
     */
    public final TableField<BankStatementRecord, UUID> PREVIOUS_STATEMENT_ID = createField(DSL.name("previous_statement_id"), SQLDataType.UUID, this, "");

    /**
     * The column <code>REGULAR_INCOME.bank_statement.created_at</code>.
     */
    public final TableField<BankStatementRecord, OffsetDateTime> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false).defaultValue(DSL.field(DSL.raw("now()"), SQLDataType.TIMESTAMPWITHTIMEZONE)), this, "");

    /**
     * The column <code>REGULAR_INCOME.bank_statement.updated_at</code>.
     */
    public final TableField<BankStatementRecord, OffsetDateTime> UPDATED_AT = createField(DSL.name("updated_at"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false).defaultValue(DSL.field(DSL.raw("now()"), SQLDataType.TIMESTAMPWITHTIMEZONE)), this, "");

    /**
     * The column <code>REGULAR_INCOME.bank_statement.owner_id</code>.
     */
    public final TableField<BankStatementRecord, UUID> OWNER_ID = createField(DSL.name("owner_id"), SQLDataType.UUID.nullable(false), this, "");

    private BankStatement(Name alias, Table<BankStatementRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private BankStatement(Name alias, Table<BankStatementRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>REGULAR_INCOME.bank_statement</code> table
     * reference
     */
    public BankStatement(String alias) {
        this(DSL.name(alias), BANK_STATEMENT);
    }

    /**
     * Create an aliased <code>REGULAR_INCOME.bank_statement</code> table
     * reference
     */
    public BankStatement(Name alias) {
        this(alias, BANK_STATEMENT);
    }

    /**
     * Create a <code>REGULAR_INCOME.bank_statement</code> table reference
     */
    public BankStatement() {
        this(DSL.name("bank_statement"), null);
    }

    public <O extends Record> BankStatement(Table<O> path, ForeignKey<O, BankStatementRecord> childPath, InverseForeignKey<O, BankStatementRecord> parentPath) {
        super(path, childPath, parentPath, BANK_STATEMENT);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class BankStatementPath extends BankStatement implements Path<BankStatementRecord> {
        public <O extends Record> BankStatementPath(Table<O> path, ForeignKey<O, BankStatementRecord> childPath, InverseForeignKey<O, BankStatementRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private BankStatementPath(Name alias, Table<BankStatementRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public BankStatementPath as(String alias) {
            return new BankStatementPath(DSL.name(alias), this);
        }

        @Override
        public BankStatementPath as(Name alias) {
            return new BankStatementPath(alias, this);
        }

        @Override
        public BankStatementPath as(Table<?> alias) {
            return new BankStatementPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : RegularIncome.REGULAR_INCOME;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.UQ_BANK_STATEMENT_ID, Indexes.UQ_BANK_STATEMENT_SINGLE_PARENT);
    }

    @Override
    public UniqueKey<BankStatementRecord> getPrimaryKey() {
        return Keys.BANK_STATEMENT_PKEY;
    }

    @Override
    public List<ForeignKey<BankStatementRecord, ?>> getReferences() {
        return Arrays.asList(Keys.BANK_STATEMENT__FK_BAK_STATEMENT_PREVIOUS_STATEMENT);
    }

    private transient BankStatementPath _bankStatement;

    /**
     * Get the implicit join path to the <code>public.bank_statement</code>
     * table.
     */
    public BankStatementPath bankStatement() {
        if (_bankStatement == null)
            _bankStatement = new BankStatementPath(this, Keys.BANK_STATEMENT__FK_BAK_STATEMENT_PREVIOUS_STATEMENT, null);

        return _bankStatement;
    }

    private transient FinancialTransactionPath _financialTransaction;

    /**
     * Get the implicit to-many join path to the
     * <code>public.financial_transaction</code> table
     */
    public FinancialTransactionPath financialTransaction() {
        if (_financialTransaction == null)
            _financialTransaction = new FinancialTransactionPath(this, null, Keys.FINANCIAL_TRANSACTION__FK_FINANCIAL_TRANSACTION_TO_BANK_STATEMENT.getInverseKey());

        return _financialTransaction;
    }

    @Override
    public BankStatement as(String alias) {
        return new BankStatement(DSL.name(alias), this);
    }

    @Override
    public BankStatement as(Name alias) {
        return new BankStatement(alias, this);
    }

    @Override
    public BankStatement as(Table<?> alias) {
        return new BankStatement(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public BankStatement rename(String name) {
        return new BankStatement(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BankStatement rename(Name name) {
        return new BankStatement(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public BankStatement rename(Table<?> name) {
        return new BankStatement(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public BankStatement where(Condition condition) {
        return new BankStatement(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public BankStatement where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public BankStatement where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public BankStatement where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public BankStatement where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public BankStatement where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public BankStatement where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public BankStatement where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public BankStatement whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public BankStatement whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
