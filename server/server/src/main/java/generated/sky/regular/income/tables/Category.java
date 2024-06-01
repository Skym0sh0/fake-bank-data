/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables;


import generated.sky.regular.income.Indexes;
import generated.sky.regular.income.Keys;
import generated.sky.regular.income.RegularIncome;
import generated.sky.regular.income.tables.Category.CategoryPath;
import generated.sky.regular.income.tables.CategoryBudget.CategoryBudgetPath;
import generated.sky.regular.income.tables.TurnoverRow.TurnoverRowPath;
import generated.sky.regular.income.tables.Users.UsersPath;
import generated.sky.regular.income.tables.records.CategoryRecord;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.jooq.Check;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.Path;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Category extends TableImpl<CategoryRecord> {

    private static final long serialVersionUID = 1L;

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
    public final TableField<CategoryRecord, UUID> ID = createField(DSL.name("id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.category.parent_category</code>.
     */
    public final TableField<CategoryRecord, UUID> PARENT_CATEGORY = createField(DSL.name("parent_category"), SQLDataType.UUID, this, "");

    /**
     * The column <code>REGULAR_INCOME.category.name</code>.
     */
    public final TableField<CategoryRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.category.is_income</code>.
     */
    public final TableField<CategoryRecord, Boolean> IS_INCOME = createField(DSL.name("is_income"), SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.category.description</code>.
     */
    public final TableField<CategoryRecord, String> DESCRIPTION = createField(DSL.name("description"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>REGULAR_INCOME.category.last_updated_at</code>.
     */
    public final TableField<CategoryRecord, OffsetDateTime> LAST_UPDATED_AT = createField(DSL.name("last_updated_at"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.category.created_at</code>.
     */
    public final TableField<CategoryRecord, OffsetDateTime> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.category.owner_id</code>.
     */
    public final TableField<CategoryRecord, UUID> OWNER_ID = createField(DSL.name("owner_id"), SQLDataType.UUID.nullable(false), this, "");

    private Category(Name alias, Table<CategoryRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Category(Name alias, Table<CategoryRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
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

    /**
     * Create a <code>REGULAR_INCOME.category</code> table reference
     */
    public Category() {
        this(DSL.name("category"), null);
    }

    public <O extends Record> Category(Table<O> path, ForeignKey<O, CategoryRecord> childPath, InverseForeignKey<O, CategoryRecord> parentPath) {
        super(path, childPath, parentPath, CATEGORY);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class CategoryPath extends Category implements Path<CategoryRecord> {
        public <O extends Record> CategoryPath(Table<O> path, ForeignKey<O, CategoryRecord> childPath, InverseForeignKey<O, CategoryRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private CategoryPath(Name alias, Table<CategoryRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public CategoryPath as(String alias) {
            return new CategoryPath(DSL.name(alias), this);
        }

        @Override
        public CategoryPath as(Name alias) {
            return new CategoryPath(alias, this);
        }

        @Override
        public CategoryPath as(Table<?> alias) {
            return new CategoryPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : RegularIncome.REGULAR_INCOME;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.IDX_CATEGORY_NAME_KEY, Indexes.UQ_CATEGORY_ID);
    }

    @Override
    public UniqueKey<CategoryRecord> getPrimaryKey() {
        return Keys.CATEGORY_PKEY;
    }

    @Override
    public List<ForeignKey<CategoryRecord, ?>> getReferences() {
        return Arrays.asList(Keys.CATEGORY__FK_CATEGORY_PARENT_CATEGORY, Keys.CATEGORY__FK_CATEGORY_OWNER);
    }

    private transient CategoryPath _category;

    /**
     * Get the implicit join path to the <code>public.category</code> table.
     */
    public CategoryPath category() {
        if (_category == null)
            _category = new CategoryPath(this, Keys.CATEGORY__FK_CATEGORY_PARENT_CATEGORY, null);

        return _category;
    }

    private transient UsersPath _users;

    /**
     * Get the implicit join path to the <code>public.users</code> table.
     */
    public UsersPath users() {
        if (_users == null)
            _users = new UsersPath(this, Keys.CATEGORY__FK_CATEGORY_OWNER, null);

        return _users;
    }

    private transient CategoryBudgetPath _categoryBudget;

    /**
     * Get the implicit to-many join path to the
     * <code>public.category_budget</code> table
     */
    public CategoryBudgetPath categoryBudget() {
        if (_categoryBudget == null)
            _categoryBudget = new CategoryBudgetPath(this, null, Keys.CATEGORY_BUDGET__FK_CATEGORY_BUDGET_TO_CATEGORY.getInverseKey());

        return _categoryBudget;
    }

    private transient TurnoverRowPath _turnoverRow;

    /**
     * Get the implicit to-many join path to the
     * <code>public.turnover_row</code> table
     */
    public TurnoverRowPath turnoverRow() {
        if (_turnoverRow == null)
            _turnoverRow = new TurnoverRowPath(this, null, Keys.TURNOVER_ROW__FK_TURNOVER_ROW_CATEGORY_FKEY.getInverseKey());

        return _turnoverRow;
    }

    @Override
    public List<Check<CategoryRecord>> getChecks() {
        return Arrays.asList(
            Internal.createCheck(this, DSL.name("category_not_self_referencing"), "(((parent_category IS NULL) OR (parent_category <> id)))", true)
        );
    }

    @Override
    public Category as(String alias) {
        return new Category(DSL.name(alias), this);
    }

    @Override
    public Category as(Name alias) {
        return new Category(alias, this);
    }

    @Override
    public Category as(Table<?> alias) {
        return new Category(alias.getQualifiedName(), this);
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

    /**
     * Rename this table
     */
    @Override
    public Category rename(Table<?> name) {
        return new Category(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Category where(Condition condition) {
        return new Category(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Category where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Category where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Category where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Category where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Category where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Category where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Category where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Category whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Category whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
