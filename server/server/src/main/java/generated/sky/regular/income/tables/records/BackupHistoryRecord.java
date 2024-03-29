/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables.records;


import generated.sky.regular.income.tables.BackupHistory;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BackupHistoryRecord extends UpdatableRecordImpl<BackupHistoryRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>REGULAR_INCOME.backup_history.id</code>.
     */
    public BackupHistoryRecord setId(UUID value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.backup_history.id</code>.
     */
    public UUID getId() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>REGULAR_INCOME.backup_history.last_check</code>.
     */
    public BackupHistoryRecord setLastCheck(OffsetDateTime value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.backup_history.last_check</code>.
     */
    public OffsetDateTime getLastCheck() {
        return (OffsetDateTime) get(1);
    }

    /**
     * Setter for <code>REGULAR_INCOME.backup_history.had_work_to_do</code>.
     */
    public BackupHistoryRecord setHadWorkToDo(Boolean value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.backup_history.had_work_to_do</code>.
     */
    public Boolean getHadWorkToDo() {
        return (Boolean) get(2);
    }

    /**
     * Setter for <code>REGULAR_INCOME.backup_history.success</code>.
     */
    public BackupHistoryRecord setSuccess(Boolean value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.backup_history.success</code>.
     */
    public Boolean getSuccess() {
        return (Boolean) get(3);
    }

    /**
     * Setter for <code>REGULAR_INCOME.backup_history.error_details</code>.
     */
    public BackupHistoryRecord setErrorDetails(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.backup_history.error_details</code>.
     */
    public String getErrorDetails() {
        return (String) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<UUID> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BackupHistoryRecord
     */
    public BackupHistoryRecord() {
        super(BackupHistory.BACKUP_HISTORY);
    }

    /**
     * Create a detached, initialised BackupHistoryRecord
     */
    public BackupHistoryRecord(UUID id, OffsetDateTime lastCheck, Boolean hadWorkToDo, Boolean success, String errorDetails) {
        super(BackupHistory.BACKUP_HISTORY);

        setId(id);
        setLastCheck(lastCheck);
        setHadWorkToDo(hadWorkToDo);
        setSuccess(success);
        setErrorDetails(errorDetails);
        resetChangedOnNotNull();
    }
}
