/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables.records;


import generated.sky.regular.income.tables.BankStatement;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BankStatementRecord extends UpdatableRecordImpl<BankStatementRecord> implements Record6<UUID, LocalDate, Integer, UUID, OffsetDateTime, OffsetDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>REGULAR_INCOME.bank_statement.id</code>.
     */
    public BankStatementRecord setId(UUID value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.bank_statement.id</code>.
     */
    public UUID getId() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>REGULAR_INCOME.bank_statement.date_record</code>.
     */
    public BankStatementRecord setDateRecord(LocalDate value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.bank_statement.date_record</code>.
     */
    public LocalDate getDateRecord() {
        return (LocalDate) get(1);
    }

    /**
     * Setter for
     * <code>REGULAR_INCOME.bank_statement.amount_balance_cents</code>.
     */
    public BankStatementRecord setAmountBalanceCents(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for
     * <code>REGULAR_INCOME.bank_statement.amount_balance_cents</code>.
     */
    public Integer getAmountBalanceCents() {
        return (Integer) get(2);
    }

    /**
     * Setter for
     * <code>REGULAR_INCOME.bank_statement.previous_statement_id</code>.
     */
    public BankStatementRecord setPreviousStatementId(UUID value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for
     * <code>REGULAR_INCOME.bank_statement.previous_statement_id</code>.
     */
    public UUID getPreviousStatementId() {
        return (UUID) get(3);
    }

    /**
     * Setter for <code>REGULAR_INCOME.bank_statement.created_at</code>.
     */
    public BankStatementRecord setCreatedAt(OffsetDateTime value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.bank_statement.created_at</code>.
     */
    public OffsetDateTime getCreatedAt() {
        return (OffsetDateTime) get(4);
    }

    /**
     * Setter for <code>REGULAR_INCOME.bank_statement.updated_at</code>.
     */
    public BankStatementRecord setUpdatedAt(OffsetDateTime value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.bank_statement.updated_at</code>.
     */
    public OffsetDateTime getUpdatedAt() {
        return (OffsetDateTime) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<UUID> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<UUID, LocalDate, Integer, UUID, OffsetDateTime, OffsetDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<UUID, LocalDate, Integer, UUID, OffsetDateTime, OffsetDateTime> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<UUID> field1() {
        return BankStatement.BANK_STATEMENT.ID;
    }

    @Override
    public Field<LocalDate> field2() {
        return BankStatement.BANK_STATEMENT.DATE_RECORD;
    }

    @Override
    public Field<Integer> field3() {
        return BankStatement.BANK_STATEMENT.AMOUNT_BALANCE_CENTS;
    }

    @Override
    public Field<UUID> field4() {
        return BankStatement.BANK_STATEMENT.PREVIOUS_STATEMENT_ID;
    }

    @Override
    public Field<OffsetDateTime> field5() {
        return BankStatement.BANK_STATEMENT.CREATED_AT;
    }

    @Override
    public Field<OffsetDateTime> field6() {
        return BankStatement.BANK_STATEMENT.UPDATED_AT;
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
    public BankStatementRecord value1(UUID value) {
        setId(value);
        return this;
    }

    @Override
    public BankStatementRecord value2(LocalDate value) {
        setDateRecord(value);
        return this;
    }

    @Override
    public BankStatementRecord value3(Integer value) {
        setAmountBalanceCents(value);
        return this;
    }

    @Override
    public BankStatementRecord value4(UUID value) {
        setPreviousStatementId(value);
        return this;
    }

    @Override
    public BankStatementRecord value5(OffsetDateTime value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    public BankStatementRecord value6(OffsetDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    @Override
    public BankStatementRecord values(UUID value1, LocalDate value2, Integer value3, UUID value4, OffsetDateTime value5, OffsetDateTime value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BankStatementRecord
     */
    public BankStatementRecord() {
        super(BankStatement.BANK_STATEMENT);
    }

    /**
     * Create a detached, initialised BankStatementRecord
     */
    public BankStatementRecord(UUID id, LocalDate dateRecord, Integer amountBalanceCents, UUID previousStatementId, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        super(BankStatement.BANK_STATEMENT);

        setId(id);
        setDateRecord(dateRecord);
        setAmountBalanceCents(amountBalanceCents);
        setPreviousStatementId(previousStatementId);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
    }
}
