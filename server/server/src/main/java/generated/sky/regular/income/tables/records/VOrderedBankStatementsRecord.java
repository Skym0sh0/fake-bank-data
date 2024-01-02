/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables.records;


import generated.sky.regular.income.tables.VOrderedBankStatements;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class VOrderedBankStatementsRecord extends TableRecordImpl<VOrderedBankStatementsRecord> implements Record7<UUID, LocalDate, Integer, UUID, OffsetDateTime, OffsetDateTime, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>REGULAR_INCOME.v_ordered_bank_statements.id</code>.
     */
    public VOrderedBankStatementsRecord setId(UUID value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.v_ordered_bank_statements.id</code>.
     */
    public UUID getId() {
        return (UUID) get(0);
    }

    /**
     * Setter for
     * <code>REGULAR_INCOME.v_ordered_bank_statements.date_record</code>.
     */
    public VOrderedBankStatementsRecord setDateRecord(LocalDate value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for
     * <code>REGULAR_INCOME.v_ordered_bank_statements.date_record</code>.
     */
    public LocalDate getDateRecord() {
        return (LocalDate) get(1);
    }

    /**
     * Setter for
     * <code>REGULAR_INCOME.v_ordered_bank_statements.amount_balance_cents</code>.
     */
    public VOrderedBankStatementsRecord setAmountBalanceCents(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for
     * <code>REGULAR_INCOME.v_ordered_bank_statements.amount_balance_cents</code>.
     */
    public Integer getAmountBalanceCents() {
        return (Integer) get(2);
    }

    /**
     * Setter for
     * <code>REGULAR_INCOME.v_ordered_bank_statements.previous_statement_id</code>.
     */
    public VOrderedBankStatementsRecord setPreviousStatementId(UUID value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for
     * <code>REGULAR_INCOME.v_ordered_bank_statements.previous_statement_id</code>.
     */
    public UUID getPreviousStatementId() {
        return (UUID) get(3);
    }

    /**
     * Setter for
     * <code>REGULAR_INCOME.v_ordered_bank_statements.created_at</code>.
     */
    public VOrderedBankStatementsRecord setCreatedAt(OffsetDateTime value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for
     * <code>REGULAR_INCOME.v_ordered_bank_statements.created_at</code>.
     */
    public OffsetDateTime getCreatedAt() {
        return (OffsetDateTime) get(4);
    }

    /**
     * Setter for
     * <code>REGULAR_INCOME.v_ordered_bank_statements.updated_at</code>.
     */
    public VOrderedBankStatementsRecord setUpdatedAt(OffsetDateTime value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for
     * <code>REGULAR_INCOME.v_ordered_bank_statements.updated_at</code>.
     */
    public OffsetDateTime getUpdatedAt() {
        return (OffsetDateTime) get(5);
    }

    /**
     * Setter for <code>REGULAR_INCOME.v_ordered_bank_statements.rank</code>.
     */
    public VOrderedBankStatementsRecord setRank(Integer value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.v_ordered_bank_statements.rank</code>.
     */
    public Integer getRank() {
        return (Integer) get(6);
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<UUID, LocalDate, Integer, UUID, OffsetDateTime, OffsetDateTime, Integer> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<UUID, LocalDate, Integer, UUID, OffsetDateTime, OffsetDateTime, Integer> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<UUID> field1() {
        return VOrderedBankStatements.V_ORDERED_BANK_STATEMENTS.ID;
    }

    @Override
    public Field<LocalDate> field2() {
        return VOrderedBankStatements.V_ORDERED_BANK_STATEMENTS.DATE_RECORD;
    }

    @Override
    public Field<Integer> field3() {
        return VOrderedBankStatements.V_ORDERED_BANK_STATEMENTS.AMOUNT_BALANCE_CENTS;
    }

    @Override
    public Field<UUID> field4() {
        return VOrderedBankStatements.V_ORDERED_BANK_STATEMENTS.PREVIOUS_STATEMENT_ID;
    }

    @Override
    public Field<OffsetDateTime> field5() {
        return VOrderedBankStatements.V_ORDERED_BANK_STATEMENTS.CREATED_AT;
    }

    @Override
    public Field<OffsetDateTime> field6() {
        return VOrderedBankStatements.V_ORDERED_BANK_STATEMENTS.UPDATED_AT;
    }

    @Override
    public Field<Integer> field7() {
        return VOrderedBankStatements.V_ORDERED_BANK_STATEMENTS.RANK;
    }

    @Override
    public UUID component1() {
        return getId();
    }

    @Override
    public LocalDate component2() {
        return getDateRecord();
    }

    @Override
    public Integer component3() {
        return getAmountBalanceCents();
    }

    @Override
    public UUID component4() {
        return getPreviousStatementId();
    }

    @Override
    public OffsetDateTime component5() {
        return getCreatedAt();
    }

    @Override
    public OffsetDateTime component6() {
        return getUpdatedAt();
    }

    @Override
    public Integer component7() {
        return getRank();
    }

    @Override
    public UUID value1() {
        return getId();
    }

    @Override
    public LocalDate value2() {
        return getDateRecord();
    }

    @Override
    public Integer value3() {
        return getAmountBalanceCents();
    }

    @Override
    public UUID value4() {
        return getPreviousStatementId();
    }

    @Override
    public OffsetDateTime value5() {
        return getCreatedAt();
    }

    @Override
    public OffsetDateTime value6() {
        return getUpdatedAt();
    }

    @Override
    public Integer value7() {
        return getRank();
    }

    @Override
    public VOrderedBankStatementsRecord value1(UUID value) {
        setId(value);
        return this;
    }

    @Override
    public VOrderedBankStatementsRecord value2(LocalDate value) {
        setDateRecord(value);
        return this;
    }

    @Override
    public VOrderedBankStatementsRecord value3(Integer value) {
        setAmountBalanceCents(value);
        return this;
    }

    @Override
    public VOrderedBankStatementsRecord value4(UUID value) {
        setPreviousStatementId(value);
        return this;
    }

    @Override
    public VOrderedBankStatementsRecord value5(OffsetDateTime value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    public VOrderedBankStatementsRecord value6(OffsetDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    @Override
    public VOrderedBankStatementsRecord value7(Integer value) {
        setRank(value);
        return this;
    }

    @Override
    public VOrderedBankStatementsRecord values(UUID value1, LocalDate value2, Integer value3, UUID value4, OffsetDateTime value5, OffsetDateTime value6, Integer value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached VOrderedBankStatementsRecord
     */
    public VOrderedBankStatementsRecord() {
        super(VOrderedBankStatements.V_ORDERED_BANK_STATEMENTS);
    }

    /**
     * Create a detached, initialised VOrderedBankStatementsRecord
     */
    public VOrderedBankStatementsRecord(UUID id, LocalDate dateRecord, Integer amountBalanceCents, UUID previousStatementId, OffsetDateTime createdAt, OffsetDateTime updatedAt, Integer rank) {
        super(VOrderedBankStatements.V_ORDERED_BANK_STATEMENTS);

        setId(id);
        setDateRecord(dateRecord);
        setAmountBalanceCents(amountBalanceCents);
        setPreviousStatementId(previousStatementId);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
        setRank(rank);
    }
}
