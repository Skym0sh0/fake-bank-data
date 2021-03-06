/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables;


import generated.sky.regular.income.Indexes;
import generated.sky.regular.income.Keys;
import generated.sky.regular.income.RegularIncome;
import generated.sky.regular.income.tables.records.CategoryRecord;

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
        "jOOQ version:3.11.12"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Category extends TableImpl<CategoryRecord> {

    private static final long serialVersionUID = 482197252;

    /**
     * The reference instance of <code>REGULAR_INCOME.category</code>
     */
    public static final Category CATEGORY = new Category();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CategoryRecord> getRecordType() {
        return CategoryRecord.class;
    }

    /**
     * The column <code>REGULAR_INCOME.category.id</code>.
     */
    public final TableField<CategoryRecord, UUID> ID = createField("id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.category.parent_category</code>.
     */
    public final TableField<CategoryRecord, UUID> PARENT_CATEGORY = createField("parent_category", org.jooq.impl.SQLDataType.UUID, this, "");

    /**
     * The column <code>REGULAR_INCOME.category.name</code>.
     */
    public final TableField<CategoryRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.category.is_income</code>.
     */
    public final TableField<CategoryRecord, Boolean> IS_INCOME = createField("is_income", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.category.description</code>.
     */
    public final TableField<CategoryRecord, String> DESCRIPTION = createField("description", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>REGULAR_INCOME.category</code> table reference
     */
    public Category() {
        this(DSL.name("category"), null);
    }

    /**
     * Create an aliased <code>REGULAR_INCOME.category</code> table reference
     */
    public Category(String alias) {
        this(DSL.name(alias), CATEGORY);
    }

    /**
     * Create an aliased <code>REGULAR_INCOME.category</code> table reference
     */
    public Category(Name alias) {
        this(alias, CATEGORY);
    }

    private Category(Name alias, Table<CategoryRecord> aliased) {
        this(alias, aliased, null);
    }

    private Category(Name alias, Table<CategoryRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Category(Table<O> child, ForeignKey<O, CategoryRecord> key) {
        super(child, key, CATEGORY);
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
        return Arrays.<Index>asList(Indexes.CATEGORY_NAME_KEY, Indexes.CATEGORY_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<CategoryRecord> getPrimaryKey() {
        return Keys.CATEGORY_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<CategoryRecord>> getKeys() {
        return Arrays.<UniqueKey<CategoryRecord>>asList(Keys.CATEGORY_PKEY, Keys.CATEGORY_NAME_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<CategoryRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<CategoryRecord, ?>>asList(Keys.CATEGORY__CATEGORY_PARENT_CATEGORY_FKEY);
    }

    public generated.sky.regular.income.tables.Category category() {
        return new generated.sky.regular.income.tables.Category(this, Keys.CATEGORY__CATEGORY_PARENT_CATEGORY_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Category as(String alias) {
        return new Category(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Category as(Name alias) {
        return new Category(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Category rename(String name) {
        return new Category(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Category rename(Name name) {
        return new Category(name, null);
    }
}
