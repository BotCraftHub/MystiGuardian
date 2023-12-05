/*
 * This file is generated by jOOQ.
 */
package io.github.yusufsdiscordbot.mystigurdian.db.tables.records;


import io.github.yusufsdiscordbot.mystigurdian.db.tables.AuditChannel;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AuditChannelRecord extends UpdatableRecordImpl<AuditChannelRecord> implements Record2<String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.audit_channel.guild_id</code>.
     */
    public AuditChannelRecord setGuildId(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.audit_channel.guild_id</code>.
     */
    public String getGuildId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.audit_channel.channel_id</code>.
     */
    public AuditChannelRecord setChannelId(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.audit_channel.channel_id</code>.
     */
    public String getChannelId() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<String, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return AuditChannel.AUDIT_CHANNEL.GUILD_ID;
    }

    @Override
    public Field<String> field2() {
        return AuditChannel.AUDIT_CHANNEL.CHANNEL_ID;
    }

    @Override
    public String component1() {
        return getGuildId();
    }

    @Override
    public String component2() {
        return getChannelId();
    }

    @Override
    public String value1() {
        return getGuildId();
    }

    @Override
    public String value2() {
        return getChannelId();
    }

    @Override
    public AuditChannelRecord value1(String value) {
        setGuildId(value);
        return this;
    }

    @Override
    public AuditChannelRecord value2(String value) {
        setChannelId(value);
        return this;
    }

    @Override
    public AuditChannelRecord values(String value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AuditChannelRecord
     */
    public AuditChannelRecord() {
        super(AuditChannel.AUDIT_CHANNEL);
    }

    /**
     * Create a detached, initialised AuditChannelRecord
     */
    public AuditChannelRecord(String guildId, String channelId) {
        super(AuditChannel.AUDIT_CHANNEL);

        setGuildId(guildId);
        setChannelId(channelId);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised AuditChannelRecord
     */
    public AuditChannelRecord(io.github.yusufsdiscordbot.mystigurdian.db.tables.pojos.AuditChannel value) {
        super(AuditChannel.AUDIT_CHANNEL);

        if (value != null) {
            setGuildId(value.getGuildId());
            setChannelId(value.getChannelId());
            resetChangedOnNotNull();
        }
    }
}
