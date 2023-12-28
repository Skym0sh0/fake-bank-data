/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables.records;


import generated.sky.regular.income.tables.TurnoverRow;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.12"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TurnoverRowRecord extends UpdatableRecordImpl<TurnoverRowRecord> implements Record10<String, UUID, LocalDate, Integer, String, String, String, String, UUID, OffsetDateTime> {

    private static final long serialVersionUID = 135864732;

    /**
     * Setter for <code>REGULAR_INCOME.turnover_row.id</code>.
     */
    public TurnoverRowRecord setId(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_row.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>REGULAR_INCOME.turnover_row.turnover_file</code>.
     */
    public TurnoverRowRecord setTurnoverFile(UUID value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_row.turnover_file</code>.
     */
    public UUID getTurnoverFile() {
        return (UUID) get(1);
    }

    /**
     * Setter for <code>REGULAR_INCOME.turnover_row.date</code>.
     */
    public TurnoverRowRecord setDate(LocalDate value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_row.date</code>.
     */
    public LocalDate getDate() {
        return (LocalDate) get(2);
    }

    /**
     * Setter for <code>REGULAR_INCOME.turnover_row.amount_value_cents</code>.
     */
    public TurnoverRowRecord setAmountValueCents(Integer value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_row.amount_value_cents</code>.
     */
    public Integer getAmountValueCents() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>REGULAR_INCOME.turnover_row.description</code>.
     */
    public TurnoverRowRecord setDescription(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_row.description</code>.
     */
    public String getDescription() {
        return (String) get(4);
    }

    /**
     * Setter for <code>REGULAR_INCOME.turnover_row.suggested_category</code>.
     */
    public TurnoverRowRecord setSuggestedCategory(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_row.suggested_category</code>.
     */
    public String getSuggestedCategory() {
        return (String) get(5);
    }

    /**
     * Setter for <code>REGULAR_INCOME.turnover_row.recipient</code>.
     */
    public TurnoverRowRecord setRecipient(String value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_row.recipient</code>.
     */
    public String getRecipient() {
        return (String) get(6);
    }

    /**
     * Setter for <code>REGULAR_INCOME.turnover_row.checksum</code>.
     */
    public TurnoverRowRecord setChecksum(String value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_row.checksum</code>.
     */
    public String getChecksum() {
        return (String) get(7);
    }

    /**
     * Setter for <code>REGULAR_INCOME.turnover_row.category_id</code>.
     */
    public TurnoverRowRecord setCategoryId(UUID value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_row.category_id</code>.
     */
    public UUID getCategoryId() {
        return (UUID) get(8);
    }

    /**
     * Setter for <code>REGULAR_INCOME.turnover_row.last_updated_at</code>.
     */
    public TurnoverRowRecord setLastUpdatedAt(OffsetDateTime value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.turnover_row.last_updated_at</code>.
     */
    public OffsetDateTime getLastUpdatedAt() {
        return (OffsetDateTime) get(9);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<String, UUID, LocalDate, Integer, String, String, String, String, UUID, OffsetDateTime> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<String, UUID, LocalDate, Integer, String, String, String, String, UUID, OffsetDateTime> valuesRow() {
        return (Row10) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return TurnoverRow.TURNOVER_ROW.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UUID> field2() {
        return TurnoverRow.TURNOVER_ROW.TURNOVER_FILE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDate> field3() {
        return TurnoverRow.TURNOVER_ROW.DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return TurnoverRow.TURNOVER_ROW.AMOUNT_VALUE_CENTS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return TurnoverRow.TURNOVER_ROW.DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return TurnoverRow.TURNOVER_ROW.SUGGESTED_CATEGORY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return TurnoverRow.TURNOVER_ROW.RECIPIENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return TurnoverRow.TURNOVER_ROW.CHECKSUM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UUID> field9() {
        return TurnoverRow.TURNOVER_ROW.CATEGORY_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<OffsetDateTime> field10() {
        return TurnoverRow.TURNOVER_ROW.LAST_UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID component2() {
        return getTurnoverFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate component3() {
        return getDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getAmountValueCents();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getSuggestedCategory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component7() {
        return getRecipient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getChecksum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID component9() {
        return getCategoryId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OffsetDateTime component10() {
        return getLastUpdatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID value2() {
        return getTurnoverFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate value3() {
        return getDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getAmountValueCents();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getSuggestedCategory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getRecipient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getChecksum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID value9() {
        return getCategoryId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OffsetDateTime value10() {
        return getLastUpdatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TurnoverRowRecord value1(String value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TurnoverRowRecord value2(UUID value) {
        setTurnoverFile(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TurnoverRowRecord value3(LocalDate value) {
        setDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TurnoverRowRecord value4(Integer value) {
        setAmountValueCents(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TurnoverRowRecord value5(String value) {
        setDescription(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TurnoverRowRecord value6(String value) {
        setSuggestedCategory(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TurnoverRowRecord value7(String value) {
        setRecipient(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TurnoverRowRecord value8(String value) {
        setChecksum(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TurnoverRowRecord value9(UUID value) {
        setCategoryId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TurnoverRowRecord value10(OffsetDateTime value) {
        setLastUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TurnoverRowRecord values(String value1, UUID value2, LocalDate value3, Integer value4, String value5, String value6, String value7, String value8, UUID value9, OffsetDateTime value10) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TurnoverRowRecord
     */
    public TurnoverRowRecord() {
        super(TurnoverRow.TURNOVER_ROW);
    }

    /**
     * Create a detached, initialised TurnoverRowRecord
     */
    public TurnoverRowRecord(String id, UUID turnoverFile, LocalDate date, Integer amountValueCents, String description, String suggestedCategory, String recipient, String checksum, UUID categoryId, OffsetDateTime lastUpdatedAt) {
        super(TurnoverRow.TURNOVER_ROW);

        set(0, id);
        set(1, turnoverFile);
        set(2, date);
        set(3, amountValueCents);
        set(4, description);
        set(5, suggestedCategory);
        set(6, recipient);
        set(7, checksum);
        set(8, categoryId);
        set(9, lastUpdatedAt);
    }
}
