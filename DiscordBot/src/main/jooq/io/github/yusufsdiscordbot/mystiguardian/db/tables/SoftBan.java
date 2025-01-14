/*
 * This file is generated by jOOQ.
 */
package io.github.yusufsdiscordbot.mystiguardian.db.tables;


import io.github.yusufsdiscordbot.mystiguardian.db.Keys;
import io.github.yusufsdiscordbot.mystiguardian.db.Public;
import io.github.yusufsdiscordbot.mystiguardian.db.tables.records.SoftBanRecord;

import java.time.OffsetDateTime;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function6;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row6;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SoftBan extends TableImpl<SoftBanRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.soft_ban</code>
     */
    public static final SoftBan SOFT_BAN = new SoftBan();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SoftBanRecord> getRecordType() {
        return SoftBanRecord.class;
    }

    /**
     * The column <code>public.soft_ban.reason</code>.
     */
    public final TableField<SoftBanRecord, String> REASON = createField(DSL.name("reason"), SQLDataType.VARCHAR(256), this, "");

    /**
     * The column <code>public.soft_ban.user_id</code>.
     */
    public final TableField<SoftBanRecord, String> USER_ID = createField(DSL.name("user_id"), SQLDataType.VARCHAR(256), this, "");

    /**
     * The column <code>public.soft_ban.guild_id</code>.
     */
    public final TableField<SoftBanRecord, String> GUILD_ID = createField(DSL.name("guild_id"), SQLDataType.VARCHAR(256), this, "");

    /**
     * The column <code>public.soft_ban.days</code>.
     */
    public final TableField<SoftBanRecord, Integer> DAYS = createField(DSL.name("days"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.soft_ban.id</code>.
     */
    public final TableField<SoftBanRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.soft_ban.time</code>.
     */
    public final TableField<SoftBanRecord, OffsetDateTime> TIME = createField(DSL.name("time"), SQLDataType.TIMESTAMPWITHTIMEZONE, this, "");

    private SoftBan(Name alias, Table<SoftBanRecord> aliased) {
        this(alias, aliased, null);
    }

    private SoftBan(Name alias, Table<SoftBanRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.soft_ban</code> table reference
     */
    public SoftBan(String alias) {
        this(DSL.name(alias), SOFT_BAN);
    }

    /**
     * Create an aliased <code>public.soft_ban</code> table reference
     */
    public SoftBan(Name alias) {
        this(alias, SOFT_BAN);
    }

    /**
     * Create a <code>public.soft_ban</code> table reference
     */
    public SoftBan() {
        this(DSL.name("soft_ban"), null);
    }

    public <O extends Record> SoftBan(Table<O> child, ForeignKey<O, SoftBanRecord> key) {
        super(child, key, SOFT_BAN);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<SoftBanRecord> getPrimaryKey() {
        return Keys.PK_SOFT_BAN;
    }

    @Override
    public SoftBan as(String alias) {
        return new SoftBan(DSL.name(alias), this);
    }

    @Override
    public SoftBan as(Name alias) {
        return new SoftBan(alias, this);
    }

    @Override
    public SoftBan as(Table<?> alias) {
        return new SoftBan(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public SoftBan rename(String name) {
        return new SoftBan(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SoftBan rename(Name name) {
        return new SoftBan(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public SoftBan rename(Table<?> name) {
        return new SoftBan(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<String, String, String, Integer, Long, OffsetDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function6<? super String, ? super String, ? super String, ? super Integer, ? super Long, ? super OffsetDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function6<? super String, ? super String, ? super String, ? super Integer, ? super Long, ? super OffsetDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
