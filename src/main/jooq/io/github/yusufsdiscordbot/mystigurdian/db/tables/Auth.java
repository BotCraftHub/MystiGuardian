/*
 * This file is generated by jOOQ.
 */
package io.github.yusufsdiscordbot.mystigurdian.db.tables;


import io.github.yusufsdiscordbot.mystigurdian.db.Keys;
import io.github.yusufsdiscordbot.mystigurdian.db.Public;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.records.AuthRecord;

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
public class Auth extends TableImpl<AuthRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.auth</code>
     */
    public static final Auth AUTH = new Auth();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AuthRecord> getRecordType() {
        return AuthRecord.class;
    }

    /**
     * The column <code>public.auth.access_token</code>.
     */
    public final TableField<AuthRecord, String> ACCESS_TOKEN = createField(DSL.name("access_token"), SQLDataType.VARCHAR(256), this, "");

    /**
     * The column <code>public.auth.refresh_token</code>.
     */
    public final TableField<AuthRecord, String> REFRESH_TOKEN = createField(DSL.name("refresh_token"), SQLDataType.VARCHAR(256), this, "");

    /**
     * The column <code>public.auth.expires_at</code>.
     */
    public final TableField<AuthRecord, Long> EXPIRES_AT = createField(DSL.name("expires_at"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.auth.user_id</code>.
     */
    public final TableField<AuthRecord, String> USER_ID = createField(DSL.name("user_id"), SQLDataType.VARCHAR(256), this, "");

    /**
     * The column <code>public.auth.id</code>.
     */
    public final TableField<AuthRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "");

    private Auth(Name alias, Table<AuthRecord> aliased) {
        this(alias, aliased, null);
    }

    private Auth(Name alias, Table<AuthRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.auth</code> table reference
     */
    public Auth(String alias) {
        this(DSL.name(alias), AUTH);
    }

    /**
     * Create an aliased <code>public.auth</code> table reference
     */
    public Auth(Name alias) {
        this(alias, AUTH);
    }

    /**
     * Create a <code>public.auth</code> table reference
     */
    public Auth() {
        this(DSL.name("auth"), null);
    }

    public <O extends Record> Auth(Table<O> child, ForeignKey<O, AuthRecord> key) {
        super(child, key, AUTH);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<AuthRecord> getPrimaryKey() {
        return Keys.PK_AUTH;
    }

    @Override
    public Auth as(String alias) {
        return new Auth(DSL.name(alias), this);
    }

    @Override
    public Auth as(Name alias) {
        return new Auth(alias, this);
    }

    @Override
    public Auth as(Table<?> alias) {
        return new Auth(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Auth rename(String name) {
        return new Auth(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Auth rename(Name name) {
        return new Auth(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Auth rename(Table<?> name) {
        return new Auth(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<String, String, Long, String, Long> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function5<? super String, ? super String, ? super Long, ? super String, ? super Long, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function5<? super String, ? super String, ? super Long, ? super String, ? super Long, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
