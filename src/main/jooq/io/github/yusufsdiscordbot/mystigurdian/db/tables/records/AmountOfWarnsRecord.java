/*
 * This file is generated by jOOQ.
 */
package io.github.yusufsdiscordbot.mystigurdian.db.tables.records;


import io.github.yusufsdiscordbot.mystigurdian.db.tables.AmountOfWarns;

import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class AmountOfWarnsRecord extends TableRecordImpl<AmountOfWarnsRecord> implements Record4<Integer, String, String, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.amount_of_warns.amount_of_warns</code>.
     */
    public AmountOfWarnsRecord setAmountOfWarns(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.amount_of_warns.amount_of_warns</code>.
     */
    public Integer getAmountOfWarns() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.amount_of_warns.user_id</code>.
     */
    public AmountOfWarnsRecord setUserId(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.amount_of_warns.user_id</code>.
     */
    public String getUserId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.amount_of_warns.guild_id</code>.
     */
    public AmountOfWarnsRecord setGuildId(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.amount_of_warns.guild_id</code>.
     */
    public String getGuildId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.amount_of_warns.id</code>.
     */
    public AmountOfWarnsRecord setId(Long value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>public.amount_of_warns.id</code>.
     */
    public Long getId() {
        return (Long) get(3);
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, String, String, Long> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Integer, String, String, Long> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return AmountOfWarns.AMOUNT_OF_WARNS.AMOUNT_OF_WARNS_;
    }

    @Override
    public Field<String> field2() {
        return AmountOfWarns.AMOUNT_OF_WARNS.USER_ID;
    }

    @Override
    public Field<String> field3() {
        return AmountOfWarns.AMOUNT_OF_WARNS.GUILD_ID;
    }

    @Override
    public Field<Long> field4() {
        return AmountOfWarns.AMOUNT_OF_WARNS.ID;
    }

    @Override
    public Integer component1() {
        return getAmountOfWarns();
    }

    @Override
    public String component2() {
        return getUserId();
    }

    @Override
    public String component3() {
        return getGuildId();
    }

    @Override
    public Long component4() {
        return getId();
    }

    @Override
    public Integer value1() {
        return getAmountOfWarns();
    }

    @Override
    public String value2() {
        return getUserId();
    }

    @Override
    public String value3() {
        return getGuildId();
    }

    @Override
    public Long value4() {
        return getId();
    }

    @Override
    public AmountOfWarnsRecord value1(Integer value) {
        setAmountOfWarns(value);
        return this;
    }

    @Override
    public AmountOfWarnsRecord value2(String value) {
        setUserId(value);
        return this;
    }

    @Override
    public AmountOfWarnsRecord value3(String value) {
        setGuildId(value);
        return this;
    }

    @Override
    public AmountOfWarnsRecord value4(Long value) {
        setId(value);
        return this;
    }

    @Override
    public AmountOfWarnsRecord values(Integer value1, String value2, String value3, Long value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AmountOfWarnsRecord
     */
    public AmountOfWarnsRecord() {
        super(AmountOfWarns.AMOUNT_OF_WARNS);
    }

    /**
     * Create a detached, initialised AmountOfWarnsRecord
     */
    public AmountOfWarnsRecord(Integer amountOfWarns, String userId, String guildId, Long id) {
        super(AmountOfWarns.AMOUNT_OF_WARNS);

        setAmountOfWarns(amountOfWarns);
        setUserId(userId);
        setGuildId(guildId);
        setId(id);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised AmountOfWarnsRecord
     */
    public AmountOfWarnsRecord(io.github.yusufsdiscordbot.mystigurdian.db.tables.pojos.AmountOfWarns value) {
        super(AmountOfWarns.AMOUNT_OF_WARNS);

        if (value != null) {
            setAmountOfWarns(value.getAmountOfWarns());
            setUserId(value.getUserId());
            setGuildId(value.getGuildId());
            setId(value.getId());
            resetChangedOnNotNull();
        }
    }
}
