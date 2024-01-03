/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables.records;


import generated.sky.regular.income.tables.VOrderedBankStatements;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class VOrderedBankStatementsRecord extends TableRecordImpl<VOrderedBankStatementsRecord> {

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
        resetChangedOnNotNull();
    }
}
