/*
 * This file is generated by jOOQ.
 */
package io.github.yusufsdiscordbot.mystiguardian.db.tables;


import io.github.yusufsdiscordbot.mystiguardian.db.Keys;
import io.github.yusufsdiscordbot.mystiguardian.db.Public;
import io.github.yusufsdiscordbot.mystiguardian.db.tables.records.OauthRecord;

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
public class Oauth extends TableImpl<OauthRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.oauth</code>
     */
    public static final Oauth OAUTH = new Oauth();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<OauthRecord> getRecordType() {
        return OauthRecord.class;
    }

    /**
     * The column <code>public.oauth.access_token</code>.
     */
    public final TableField<OauthRecord, String> ACCESS_TOKEN = createField(DSL.name("access_token"), SQLDataType.VARCHAR(256), this, "");

    /**
     * The column <code>public.oauth.refresh_token</code>.
     */
    public final TableField<OauthRecord, String> REFRESH_TOKEN = createField(DSL.name("refresh_token"), SQLDataType.VARCHAR(256), this, "");

    /**
     * The column <code>public.oauth.user_json</code>.
     */
    public final TableField<OauthRecord, String> USER_JSON = createField(DSL.name("user_json"), SQLDataType.VARCHAR(1000), this, "");

    /**
     * The column <code>public.oauth.user_id</code>.
     */
    public final TableField<OauthRecord, String> USER_ID = createField(DSL.name("user_id"), SQLDataType.VARCHAR(256), this, "");

    /**
     * The column <code>public.oauth.id</code>.
     */
    public final TableField<OauthRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.oauth.expires_in</code>.
     */
    public final TableField<OauthRecord, String> EXPIRES_IN = createField(DSL.name("expires_in"), SQLDataType.VARCHAR(256), this, "");

    private Oauth(Name alias, Table<OauthRecord> aliased) {
        this(alias, aliased, null);
    }

    private Oauth(Name alias, Table<OauthRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.oauth</code> table reference
     */
    public Oauth(String alias) {
        this(DSL.name(alias), OAUTH);
    }

    /**
     * Create an aliased <code>public.oauth</code> table reference
     */
    public Oauth(Name alias) {
        this(alias, OAUTH);
    }

    /**
     * Create a <code>public.oauth</code> table reference
     */
    public Oauth() {
        this(DSL.name("oauth"), null);
    }

    public <O extends Record> Oauth(Table<O> child, ForeignKey<O, OauthRecord> key) {
        super(child, key, OAUTH);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<OauthRecord> getPrimaryKey() {
        return Keys.PK_OAUTH;
    }

    @Override
    public Oauth as(String alias) {
        return new Oauth(DSL.name(alias), this);
    }

    @Override
    public Oauth as(Name alias) {
        return new Oauth(alias, this);
    }

    @Override
    public Oauth as(Table<?> alias) {
        return new Oauth(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Oauth rename(String name) {
        return new Oauth(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Oauth rename(Name name) {
        return new Oauth(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Oauth rename(Table<?> name) {
        return new Oauth(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<String, String, String, String, Long, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function6<? super String, ? super String, ? super String, ? super String, ? super Long, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function6<? super String, ? super String, ? super String, ? super String, ? super Long, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}