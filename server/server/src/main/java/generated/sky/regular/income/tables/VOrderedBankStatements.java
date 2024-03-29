/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables;


import generated.sky.regular.income.RegularIncome;
import generated.sky.regular.income.tables.records.VOrderedBankStatementsRecord;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.UUID;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class VOrderedBankStatements extends TableImpl<VOrderedBankStatementsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of
     * <code>REGULAR_INCOME.v_ordered_bank_statements</code>
     */
    public static final VOrderedBankStatements V_ORDERED_BANK_STATEMENTS = new VOrderedBankStatements();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<VOrderedBankStatementsRecord> getRecordType() {
        return VOrderedBankStatementsRecord.class;
    }

    /**
     * The column <code>REGULAR_INCOME.v_ordered_bank_statements.id</code>.
     */
    public final TableField<VOrderedBankStatementsRecord, UUID> ID = createField(DSL.name("id"), SQLDataType.UUID, this, "");

    /**
     * The column
     * <code>REGULAR_INCOME.v_ordered_bank_statements.date_record</code>.
     */
    public final TableField<VOrderedBankStatementsRecord, LocalDate> DATE_RECORD = createField(DSL.name("date_record"), SQLDataType.LOCALDATE, this, "");

    /**
     * The column
     * <code>REGULAR_INCOME.v_ordered_bank_statements.amount_balance_cents</code>.
     */
    public final TableField<VOrderedBankStatementsRecord, Integer> AMOUNT_BALANCE_CENTS = createField(DSL.name("amount_balance_cents"), SQLDataType.INTEGER, this, "");

    /**
     * The column
     * <code>REGULAR_INCOME.v_ordered_bank_statements.previous_statement_id</code>.
     */
    public final TableField<VOrderedBankStatementsRecord, UUID> PREVIOUS_STATEMENT_ID = createField(DSL.name("previous_statement_id"), SQLDataType.UUID, this, "");

    /**
     * The column
     * <code>REGULAR_INCOME.v_ordered_bank_statements.created_at</code>.
     */
    public final TableField<VOrderedBankStatementsRecord, OffsetDateTime> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.TIMESTAMPWITHTIMEZONE(6), this, "");

    /**
     * The column
     * <code>REGULAR_INCOME.v_ordered_bank_statements.updated_at</code>.
     */
    public final TableField<VOrderedBankStatementsRecord, OffsetDateTime> UPDATED_AT = createField(DSL.name("updated_at"), SQLDataType.TIMESTAMPWITHTIMEZONE(6), this, "");

    /**
     * The column
     * <code>REGULAR_INCOME.v_ordered_bank_statements.owner_id</code>.
     */
    public final TableField<VOrderedBankStatementsRecord, UUID> OWNER_ID = createField(DSL.name("owner_id"), SQLDataType.UUID, this, "");

    /**
     * The column <code>REGULAR_INCOME.v_ordered_bank_statements.rank</code>.
     */
    public final TableField<VOrderedBankStatementsRecord, Integer> RANK = createField(DSL.name("rank"), SQLDataType.INTEGER, this, "");

    private VOrderedBankStatements(Name alias, Table<VOrderedBankStatementsRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private VOrderedBankStatements(Name alias, Table<VOrderedBankStatementsRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("""
        create view "v_ordered_bank_statements" as  WITH RECURSIVE cte AS (
                SELECT bank_statement.id,
                   bank_statement.date_record,
                   bank_statement.amount_balance_cents,
                   bank_statement.previous_statement_id,
                   bank_statement.created_at,
                   bank_statement.updated_at,
                   bank_statement.owner_id,
                   0 AS rank
                  FROM bank_statement
                 WHERE (bank_statement.previous_statement_id IS NULL)
               UNION ALL
                SELECT e.id,
                   e.date_record,
                   e.amount_balance_cents,
                   e.previous_statement_id,
                   e.created_at,
                   e.updated_at,
                   e.owner_id,
                   (c.rank + 1)
                  FROM (cte c
                    JOIN bank_statement e ON ((e.previous_statement_id = c.id)))
               )
        SELECT id,
           date_record,
           amount_balance_cents,
           previous_statement_id,
           created_at,
           updated_at,
           owner_id,
           rank
          FROM cte;
        """), where);
    }

    /**
     * Create an aliased <code>REGULAR_INCOME.v_ordered_bank_statements</code>
     * table reference
     */
    public VOrderedBankStatements(String alias) {
        this(DSL.name(alias), V_ORDERED_BANK_STATEMENTS);
    }

    /**
     * Create an aliased <code>REGULAR_INCOME.v_ordered_bank_statements</code>
     * table reference
     */
    public VOrderedBankStatements(Name alias) {
        this(alias, V_ORDERED_BANK_STATEMENTS);
    }

    /**
     * Create a <code>REGULAR_INCOME.v_ordered_bank_statements</code> table
     * reference
     */
    public VOrderedBankStatements() {
        this(DSL.name("v_ordered_bank_statements"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : RegularIncome.REGULAR_INCOME;
    }

    @Override
    public VOrderedBankStatements as(String alias) {
        return new VOrderedBankStatements(DSL.name(alias), this);
    }

    @Override
    public VOrderedBankStatements as(Name alias) {
        return new VOrderedBankStatements(alias, this);
    }

    @Override
    public VOrderedBankStatements as(Table<?> alias) {
        return new VOrderedBankStatements(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public VOrderedBankStatements rename(String name) {
        return new VOrderedBankStatements(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public VOrderedBankStatements rename(Name name) {
        return new VOrderedBankStatements(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public VOrderedBankStatements rename(Table<?> name) {
        return new VOrderedBankStatements(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public VOrderedBankStatements where(Condition condition) {
        return new VOrderedBankStatements(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public VOrderedBankStatements where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public VOrderedBankStatements where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public VOrderedBankStatements where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public VOrderedBankStatements where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public VOrderedBankStatements where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public VOrderedBankStatements where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public VOrderedBankStatements where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public VOrderedBankStatements whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public VOrderedBankStatements whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
