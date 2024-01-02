/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables.records;


import generated.sky.regular.income.tables.TurnoverFileImport;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TurnoverFileImportRecord extends UpdatableRecordImpl<TurnoverFileImportRecord> implements Record8<UUID, OffsetDateTime, String, Long, String, byte[], String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>REGULAR_INCOME.turnover_file_import.id</code>.
     */
    public TurnoverFileImportRecord setId(UUID value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_file_import.id</code>.
     */
    public UUID getId() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>REGULAR_INCOME.turnover_file_import.imported_at</code>.
     */
    public TurnoverFileImportRecord setImportedAt(OffsetDateTime value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_file_import.imported_at</code>.
     */
    public OffsetDateTime getImportedAt() {
        return (OffsetDateTime) get(1);
    }

    /**
     * Setter for <code>REGULAR_INCOME.turnover_file_import.filename</code>.
     */
    public TurnoverFileImportRecord setFilename(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_file_import.filename</code>.
     */
    public String getFilename() {
        return (String) get(2);
    }

    /**
     * Setter for <code>REGULAR_INCOME.turnover_file_import.file_size</code>.
     */
    public TurnoverFileImportRecord setFileSize(Long value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_file_import.file_size</code>.
     */
    public Long getFileSize() {
        return (Long) get(3);
    }

    /**
     * Setter for
     * <code>REGULAR_INCOME.turnover_file_import.file_content_type</code>.
     */
    public TurnoverFileImportRecord setFileContentType(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for
     * <code>REGULAR_INCOME.turnover_file_import.file_content_type</code>.
     */
    public String getFileContentType() {
        return (String) get(4);
    }

    /**
     * Setter for <code>REGULAR_INCOME.turnover_file_import.file_content</code>.
     */
    public TurnoverFileImportRecord setFileContent(byte[] value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_file_import.file_content</code>.
     */
    public byte[] getFileContent() {
        return (byte[]) get(5);
    }

    /**
     * Setter for <code>REGULAR_INCOME.turnover_file_import.checksum</code>.
     */
    public TurnoverFileImportRecord setChecksum(String value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_file_import.checksum</code>.
     */
    public String getChecksum() {
        return (String) get(6);
    }

    /**
     * Setter for
     * <code>REGULAR_INCOME.turnover_file_import.turnover_file_format</code>.
     */
    public TurnoverFileImportRecord setTurnoverFileFormat(String value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for
     * <code>REGULAR_INCOME.turnover_file_import.turnover_file_format</code>.
     */
    public String getTurnoverFileFormat() {
        return (String) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<UUID> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<UUID, OffsetDateTime, String, Long, String, byte[], String, String> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<UUID, OffsetDateTime, String, Long, String, byte[], String, String> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<UUID> field1() {
        return TurnoverFileImport.TURNOVER_FILE_IMPORT.ID;
    }

    @Override
    public Field<OffsetDateTime> field2() {
        return TurnoverFileImport.TURNOVER_FILE_IMPORT.IMPORTED_AT;
    }

    @Override
    public Field<String> field3() {
        return TurnoverFileImport.TURNOVER_FILE_IMPORT.FILENAME;
    }

    @Override
    public Field<Long> field4() {
        return TurnoverFileImport.TURNOVER_FILE_IMPORT.FILE_SIZE;
    }

    @Override
    public Field<String> field5() {
        return TurnoverFileImport.TURNOVER_FILE_IMPORT.FILE_CONTENT_TYPE;
    }

    @Override
    public Field<byte[]> field6() {
        return TurnoverFileImport.TURNOVER_FILE_IMPORT.FILE_CONTENT;
    }

    @Override
    public Field<String> field7() {
        return TurnoverFileImport.TURNOVER_FILE_IMPORT.CHECKSUM;
    }

    @Override
    public Field<String> field8() {
        return TurnoverFileImport.TURNOVER_FILE_IMPORT.TURNOVER_FILE_FORMAT;
    }

    @Override
    public UUID component1() {
        return getId();
    }

    @Override
    public OffsetDateTime component2() {
        return getImportedAt();
    }

    @Override
    public String component3() {
        return getFilename();
    }

    @Override
    public Long component4() {
        return getFileSize();
    }

    @Override
    public String component5() {
        return getFileContentType();
    }

    @Override
    public byte[] component6() {
        return getFileContent();
    }

    @Override
    public String component7() {
        return getChecksum();
    }

    @Override
    public String component8() {
        return getTurnoverFileFormat();
    }

    @Override
    public UUID value1() {
        return getId();
    }

    @Override
    public OffsetDateTime value2() {
        return getImportedAt();
    }

    @Override
    public String value3() {
        return getFilename();
    }

    @Override
    public Long value4() {
        return getFileSize();
    }

    @Override
    public String value5() {
        return getFileContentType();
    }

    @Override
    public byte[] value6() {
        return getFileContent();
    }

    @Override
    public String value7() {
        return getChecksum();
    }

    @Override
    public String value8() {
        return getTurnoverFileFormat();
    }

    @Override
    public TurnoverFileImportRecord value1(UUID value) {
        setId(value);
        return this;
    }

    @Override
    public TurnoverFileImportRecord value2(OffsetDateTime value) {
        setImportedAt(value);
        return this;
    }

    @Override
    public TurnoverFileImportRecord value3(String value) {
        setFilename(value);
        return this;
    }

    @Override
    public TurnoverFileImportRecord value4(Long value) {
        setFileSize(value);
        return this;
    }

    @Override
    public TurnoverFileImportRecord value5(String value) {
        setFileContentType(value);
        return this;
    }

    @Override
    public TurnoverFileImportRecord value6(byte[] value) {
        setFileContent(value);
        return this;
    }

    @Override
    public TurnoverFileImportRecord value7(String value) {
        setChecksum(value);
        return this;
    }

    @Override
    public TurnoverFileImportRecord value8(String value) {
        setTurnoverFileFormat(value);
        return this;
    }

    @Override
    public TurnoverFileImportRecord values(UUID value1, OffsetDateTime value2, String value3, Long value4, String value5, byte[] value6, String value7, String value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TurnoverFileImportRecord
     */
    public TurnoverFileImportRecord() {
        super(TurnoverFileImport.TURNOVER_FILE_IMPORT);
    }

    /**
     * Create a detached, initialised TurnoverFileImportRecord
     */
    public TurnoverFileImportRecord(UUID id, OffsetDateTime importedAt, String filename, Long fileSize, String fileContentType, byte[] fileContent, String checksum, String turnoverFileFormat) {
        super(TurnoverFileImport.TURNOVER_FILE_IMPORT);

        setId(id);
        setImportedAt(importedAt);
        setFilename(filename);
        setFileSize(fileSize);
        setFileContentType(fileContentType);
        setFileContent(fileContent);
        setChecksum(checksum);
        setTurnoverFileFormat(turnoverFileFormat);
    }
}
