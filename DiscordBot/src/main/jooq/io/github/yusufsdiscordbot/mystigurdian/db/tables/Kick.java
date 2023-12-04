/*
 * This file is generated by jOOQ.
 */
package io.github.yusufsdiscordbot.mystigurdian.db.tables;


import io.github.yusufsdiscordbot.mystigurdian.db.Keys;
import io.github.yusufsdiscordbot.mystigurdian.db.Public;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.records.KickRecord;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function5;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row5;
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
public class Kick extends TableImpl<KickRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.kick</code>
     */
    public static final Kick KICK = new Kick();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<KickRecord> getRecordType() {
        return KickRecord.class;
    }

    /**
     * The column <code>public.kick.reason</code>.
     */
    public final TableField<KickRecord, String> REASON = createField(DSL.name("reason"), SQLDataType.VARCHAR(256), this, "");

    /**
     * The column <code>public.kick.user_id</code>.
     */
    public final TableField<KickRecord, String> USER_ID = createField(DSL.name("user_id"), SQLDataType.VARCHAR(256), this, "");

    /**
     * The column <code>public.kick.guild_id</code>.
     */
    public final TableField<KickRecord, String> GUILD_ID = createField(DSL.name("guild_id"), SQLDataType.VARCHAR(256), this, "");

    /**
     * The column <code>public.kick.id</code>.
     */
    public final TableField<KickRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.kick.time</code>.
     */
    public final TableField<KickRecord, OffsetDateTime> TIME = createField(DSL.name("time"), SQLDataType.TIMESTAMPWITHTIMEZONE, this, "");

    private Kick(Name alias, Table<KickRecord> aliased) {
        this(alias, aliased, null);
    }

    private Kick(Name alias, Table<KickRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.kick</code> table reference
     */
    public Kick(String alias) {
        this(DSL.name(alias), KICK);
    }

    /**
     * Create an aliased <code>public.kick</code> table reference
     */
    public Kick(Name alias) {
        this(alias, KICK);
    }

    /**
     * Create a <code>public.kick</code> table reference
     */
    public Kick() {
        this(DSL.name("kick"), null);
    }

    public <O extends Record> Kick(Table<O> child, ForeignKey<O, KickRecord> key) {
        super(child, key, KICK);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<KickRecord> getPrimaryKey() {
        return Keys.PK_KICK;
    }

    @Override
    public List<UniqueKey<KickRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.UK_KICK);
    }

    @Override
    public Kick as(String alias) {
        return new Kick(DSL.name(alias), this);
    }

    @Override
    public Kick as(Name alias) {
        return new Kick(alias, this);
    }

    @Override
    public Kick as(Table<?> alias) {
        return new Kick(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Kick rename(String name) {
        return new Kick(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Kick rename(Name name) {
        return new Kick(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Kick rename(Table<?> name) {
        return new Kick(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<String, String, String, Long, OffsetDateTime> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function5<? super String, ? super String, ? super String, ? super Long, ? super OffsetDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function5<? super String, ? super String, ? super String, ? super Long, ? super OffsetDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}