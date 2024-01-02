/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables.records;


import generated.sky.regular.income.tables.Category;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CategoryRecord extends UpdatableRecordImpl<CategoryRecord> implements Record7<UUID, UUID, String, Boolean, String, OffsetDateTime, OffsetDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>REGULAR_INCOME.category.id</code>.
     */
    public CategoryRecord setId(UUID value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.category.id</code>.
     */
    public UUID getId() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>REGULAR_INCOME.category.parent_category</code>.
     */
    public CategoryRecord setParentCategory(UUID value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.category.parent_category</code>.
     */
    public UUID getParentCategory() {
        return (UUID) get(1);
    }

    /**
     * Setter for <code>REGULAR_INCOME.category.name</code>.
     */
    public CategoryRecord setName(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.category.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>REGULAR_INCOME.category.is_income</code>.
     */
    public CategoryRecord setIsIncome(Boolean value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.category.is_income</code>.
     */
    public Boolean getIsIncome() {
        return (Boolean) get(3);
    }

    /**
     * Setter for <code>REGULAR_INCOME.category.description</code>.
     */
    public CategoryRecord setDescription(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.category.description</code>.
     */
    public String getDescription() {
        return (String) get(4);
    }

    /**
     * Setter for <code>REGULAR_INCOME.category.last_updated_at</code>.
     */
    public CategoryRecord setLastUpdatedAt(OffsetDateTime value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.category.last_updated_at</code>.
     */
    public OffsetDateTime getLastUpdatedAt() {
        return (OffsetDateTime) get(5);
    }

    /**
     * Setter for <code>REGULAR_INCOME.category.created_at</code>.
     */
    public CategoryRecord setCreatedAt(OffsetDateTime value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>REGULAR_INCOME.category.created_at</code>.
     */
    public OffsetDateTime getCreatedAt() {
        return (OffsetDateTime) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<UUID> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<UUID, UUID, String, Boolean, String, OffsetDateTime, OffsetDateTime> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<UUID, UUID, String, Boolean, String, OffsetDateTime, OffsetDateTime> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<UUID> field1() {
        return Category.CATEGORY.ID;
    }

    @Override
    public Field<UUID> field2() {
        return Category.CATEGORY.PARENT_CATEGORY;
    }

    @Override
    public Field<String> field3() {
        return Category.CATEGORY.NAME;
    }

    @Override
    public Field<Boolean> field4() {
        return Category.CATEGORY.IS_INCOME;
    }

    @Override
    public Field<String> field5() {
        return Category.CATEGORY.DESCRIPTION;
    }

    @Override
    public Field<OffsetDateTime> field6() {
        return Category.CATEGORY.LAST_UPDATED_AT;
    }

    @Override
    public Field<OffsetDateTime> field7() {
        return Category.CATEGORY.CREATED_AT;
    }

    @Override
    public UUID component1() {
        return getId();
    }

    @Override
    public UUID component2() {
        return getParentCategory();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public Boolean component4() {
        return getIsIncome();
    }

    @Override
    public String component5() {
        return getDescription();
    }

    @Override
    public OffsetDateTime component6() {
        return getLastUpdatedAt();
    }

    @Override
    public OffsetDateTime component7() {
        return getCreatedAt();
    }

    @Override
    public UUID value1() {
        return getId();
    }

    @Override
    public UUID value2() {
        return getParentCategory();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public Boolean value4() {
        return getIsIncome();
    }

    @Override
    public String value5() {
        return getDescription();
    }

    @Override
    public OffsetDateTime value6() {
        return getLastUpdatedAt();
    }

    @Override
    public OffsetDateTime value7() {
        return getCreatedAt();
    }

    @Override
    public CategoryRecord value1(UUID value) {
        setId(value);
        return this;
    }

    @Override
    public CategoryRecord value2(UUID value) {
        setParentCategory(value);
        return this;
    }

    @Override
    public CategoryRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public CategoryRecord value4(Boolean value) {
        setIsIncome(value);
        return this;
    }

    @Override
    public CategoryRecord value5(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public CategoryRecord value6(OffsetDateTime value) {
        setLastUpdatedAt(value);
        return this;
    }

    @Override
    public CategoryRecord value7(OffsetDateTime value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    public CategoryRecord values(UUID value1, UUID value2, String value3, Boolean value4, String value5, OffsetDateTime value6, OffsetDateTime value7) {
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
     * Create a detached CategoryRecord
     */
    public CategoryRecord() {
        super(Category.CATEGORY);
    }

    /**
     * Create a detached, initialised CategoryRecord
     */
    public CategoryRecord(UUID id, UUID parentCategory, String name, Boolean isIncome, String description, OffsetDateTime lastUpdatedAt, OffsetDateTime createdAt) {
        super(Category.CATEGORY);

        setId(id);
        setParentCategory(parentCategory);
        setName(name);
        setIsIncome(isIncome);
        setDescription(description);
        setLastUpdatedAt(lastUpdatedAt);
        setCreatedAt(createdAt);
    }
}
