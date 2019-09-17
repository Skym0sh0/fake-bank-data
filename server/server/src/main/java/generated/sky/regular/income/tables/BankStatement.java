/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables;


import de.sky.common.database.converters.DateConverter;

import generated.sky.regular.income.Indexes;
import generated.sky.regular.income.Keys;
import generated.sky.regular.income.RegularIncome;
import generated.sky.regular.income.tables.records.BankStatementRecord;

import java.time.LocalDate;
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
public class BankStatement extends TableImpl<BankStatementRecord> {

    private static final long serialVersionUID = 846205934;

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
    public final TableField<BankStatementRecord, UUID> ID = createField("id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.bank_statement.date_record</code>.
     */
    public final TableField<BankStatementRecord, LocalDate> DATE_RECORD = createField("date_record", org.jooq.impl.SQLDataType.DATE.nullable(false), this, "", new DateConverter());

    /**
     * The column <code>REGULAR_INCOME.bank_statement.amount_balance_cents</code>.
     */
    public final TableField<BankStatementRecord, Integer> AMOUNT_BALANCE_CENTS = createField("amount_balance_cents", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.bank_statement.previous_statement_id</code>.
     */
    public final TableField<BankStatementRecord, UUID> PREVIOUS_STATEMENT_ID = createField("previous_statement_id", org.jooq.impl.SQLDataType.UUID, this, "");

    /**
     * Create a <code>REGULAR_INCOME.bank_statement</code> table reference
     */
    public BankStatement() {
        this(DSL.name("bank_statement"), null);
    }

    /**
     * Create an aliased <code>REGULAR_INCOME.bank_statement</code> table reference
     */
    public BankStatement(String alias) {
        this(DSL.name(alias), BANK_STATEMENT);
    }

    /**
     * Create an aliased <code>REGULAR_INCOME.bank_statement</code> table reference
     */
    public BankStatement(Name alias) {
        this(alias, BANK_STATEMENT);
    }

    private BankStatement(Name alias, Table<BankStatementRecord> aliased) {
        this(alias, aliased, null);
    }

    private BankStatement(Name alias, Table<BankStatementRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> BankStatement(Table<O> child, ForeignKey<O, BankStatementRecord> key) {
        super(child, key, BANK_STATEMENT);
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
        return Arrays.<Index>asList(Indexes.BANK_STATEMENT_PKEY, Indexes.BANK_STATEMENT_PREVIOUS_STATEMENT_ID_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<BankStatementRecord> getPrimaryKey() {
        return Keys.BANK_STATEMENT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<BankStatementRecord>> getKeys() {
        return Arrays.<UniqueKey<BankStatementRecord>>asList(Keys.BANK_STATEMENT_PKEY, Keys.BANK_STATEMENT_PREVIOUS_STATEMENT_ID_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<BankStatementRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BankStatementRecord, ?>>asList(Keys.BANK_STATEMENT__BANK_STATEMENT_PREVIOUS_STATEMENT_ID_FKEY);
    }

    public generated.sky.regular.income.tables.BankStatement bankStatement() {
        return new generated.sky.regular.income.tables.BankStatement(this, Keys.BANK_STATEMENT__BANK_STATEMENT_PREVIOUS_STATEMENT_ID_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BankStatement as(String alias) {
        return new BankStatement(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BankStatement as(Name alias) {
        return new BankStatement(alias, this);
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
}
