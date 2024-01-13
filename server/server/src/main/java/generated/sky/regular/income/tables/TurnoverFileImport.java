/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.tables;


import generated.sky.regular.income.Indexes;
import generated.sky.regular.income.Keys;
import generated.sky.regular.income.RegularIncome;
import generated.sky.regular.income.tables.TurnoverRow.TurnoverRowPath;
import generated.sky.regular.income.tables.Users.UsersPath;
import generated.sky.regular.income.tables.records.TurnoverFileImportRecord;

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
public class TurnoverFileImport extends TableImpl<TurnoverFileImportRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of
     * <code>REGULAR_INCOME.turnover_file_import</code>
     */
    public static final TurnoverFileImport TURNOVER_FILE_IMPORT = new TurnoverFileImport();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TurnoverFileImportRecord> getRecordType() {
        return TurnoverFileImportRecord.class;
    }

    /**
     * The column <code>REGULAR_INCOME.turnover_file_import.id</code>.
     */
    public final TableField<TurnoverFileImportRecord, UUID> ID = createField(DSL.name("id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.turnover_file_import.imported_at</code>.
     */
    public final TableField<TurnoverFileImportRecord, OffsetDateTime> IMPORTED_AT = createField(DSL.name("imported_at"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.turnover_file_import.filename</code>.
     */
    public final TableField<TurnoverFileImportRecord, String> FILENAME = createField(DSL.name("filename"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.turnover_file_import.file_size</code>.
     */
    public final TableField<TurnoverFileImportRecord, Long> FILE_SIZE = createField(DSL.name("file_size"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column
     * <code>REGULAR_INCOME.turnover_file_import.file_content_type</code>.
     */
    public final TableField<TurnoverFileImportRecord, String> FILE_CONTENT_TYPE = createField(DSL.name("file_content_type"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.turnover_file_import.file_content</code>.
     */
    public final TableField<TurnoverFileImportRecord, byte[]> FILE_CONTENT = createField(DSL.name("file_content"), SQLDataType.BLOB.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.turnover_file_import.checksum</code>.
     */
    public final TableField<TurnoverFileImportRecord, String> CHECKSUM = createField(DSL.name("checksum"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column
     * <code>REGULAR_INCOME.turnover_file_import.turnover_file_format</code>.
     */
    public final TableField<TurnoverFileImportRecord, String> TURNOVER_FILE_FORMAT = createField(DSL.name("turnover_file_format"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>REGULAR_INCOME.turnover_file_import.owner_id</code>.
     */
    public final TableField<TurnoverFileImportRecord, UUID> OWNER_ID = createField(DSL.name("owner_id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column
     * <code>REGULAR_INCOME.turnover_file_import.file_encoding</code>.
     */
    public final TableField<TurnoverFileImportRecord, String> FILE_ENCODING = createField(DSL.name("file_encoding"), SQLDataType.CLOB.nullable(false), this, "");

    private TurnoverFileImport(Name alias, Table<TurnoverFileImportRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private TurnoverFileImport(Name alias, Table<TurnoverFileImportRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>REGULAR_INCOME.turnover_file_import</code> table
     * reference
     */
    public TurnoverFileImport(String alias) {
        this(DSL.name(alias), TURNOVER_FILE_IMPORT);
    }

    /**
     * Create an aliased <code>REGULAR_INCOME.turnover_file_import</code> table
     * reference
     */
    public TurnoverFileImport(Name alias) {
        this(alias, TURNOVER_FILE_IMPORT);
    }

    /**
     * Create a <code>REGULAR_INCOME.turnover_file_import</code> table reference
     */
    public TurnoverFileImport() {
        this(DSL.name("turnover_file_import"), null);
    }

    public <O extends Record> TurnoverFileImport(Table<O> path, ForeignKey<O, TurnoverFileImportRecord> childPath, InverseForeignKey<O, TurnoverFileImportRecord> parentPath) {
        super(path, childPath, parentPath, TURNOVER_FILE_IMPORT);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class TurnoverFileImportPath extends TurnoverFileImport implements Path<TurnoverFileImportRecord> {
        public <O extends Record> TurnoverFileImportPath(Table<O> path, ForeignKey<O, TurnoverFileImportRecord> childPath, InverseForeignKey<O, TurnoverFileImportRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private TurnoverFileImportPath(Name alias, Table<TurnoverFileImportRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public TurnoverFileImportPath as(String alias) {
            return new TurnoverFileImportPath(DSL.name(alias), this);
        }

        @Override
        public TurnoverFileImportPath as(Name alias) {
            return new TurnoverFileImportPath(alias, this);
        }

        @Override
        public TurnoverFileImportPath as(Table<?> alias) {
            return new TurnoverFileImportPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : RegularIncome.REGULAR_INCOME;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.IDX_TURNOVER_FILE_IMPORT_OWNER, Indexes.UQ_TURNOVER_FILE_ID);
    }

    @Override
    public UniqueKey<TurnoverFileImportRecord> getPrimaryKey() {
        return Keys.TURNOVER_FILE_IMPORT_PKEY;
    }

    @Override
    public List<ForeignKey<TurnoverFileImportRecord, ?>> getReferences() {
        return Arrays.asList(Keys.TURNOVER_FILE_IMPORT__FK_TURNOVER_FILE_IMPORT_OWNER);
    }

    private transient UsersPath _users;

    /**
     * Get the implicit join path to the <code>public.users</code> table.
     */
    public UsersPath users() {
        if (_users == null)
            _users = new UsersPath(this, Keys.TURNOVER_FILE_IMPORT__FK_TURNOVER_FILE_IMPORT_OWNER, null);

        return _users;
    }

    private transient TurnoverRowPath _turnoverRow;

    /**
     * Get the implicit to-many join path to the
     * <code>public.turnover_row</code> table
     */
    public TurnoverRowPath turnoverRow() {
        if (_turnoverRow == null)
            _turnoverRow = new TurnoverRowPath(this, null, Keys.TURNOVER_ROW__FK_TURNOVER_ROW_FILE_FKEY.getInverseKey());

        return _turnoverRow;
    }

    @Override
    public List<Check<TurnoverFileImportRecord>> getChecks() {
        return Arrays.asList(
            Internal.createCheck(this, DSL.name("turnover_file_import_turnover_file_format_check"), "((turnover_file_format = ANY (ARRAY['VR_BANK_CSV'::text, 'DKB'::text, 'PAYPAL'::text])))", true)
        );
    }

    @Override
    public TurnoverFileImport as(String alias) {
        return new TurnoverFileImport(DSL.name(alias), this);
    }

    @Override
    public TurnoverFileImport as(Name alias) {
        return new TurnoverFileImport(alias, this);
    }

    @Override
    public TurnoverFileImport as(Table<?> alias) {
        return new TurnoverFileImport(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public TurnoverFileImport rename(String name) {
        return new TurnoverFileImport(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TurnoverFileImport rename(Name name) {
        return new TurnoverFileImport(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public TurnoverFileImport rename(Table<?> name) {
        return new TurnoverFileImport(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TurnoverFileImport where(Condition condition) {
        return new TurnoverFileImport(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TurnoverFileImport where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TurnoverFileImport where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TurnoverFileImport where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TurnoverFileImport where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TurnoverFileImport where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TurnoverFileImport where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TurnoverFileImport where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TurnoverFileImport whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TurnoverFileImport whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
