/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.walker.domain.generated.tables.records;


import com.rbkmoney.walker.domain.generated.tables.Action;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ActionRecord extends TableRecordImpl<ActionRecord> implements Record10<Long, Long, String, Timestamp, String, String, String, String, String, String> {

    private static final long serialVersionUID = 256501745;

    /**
     * Setter for <code>walk.action.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>walk.action.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>walk.action.claim_id</code>.
     */
    public void setClaimId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>walk.action.claim_id</code>.
     */
    public Long getClaimId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>walk.action.party_id</code>.
     */
    public void setPartyId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>walk.action.party_id</code>.
     */
    public String getPartyId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>walk.action.created_at</code>.
     */
    public void setCreatedAt(Timestamp value) {
        set(3, value);
    }

    /**
     * Getter for <code>walk.action.created_at</code>.
     */
    public Timestamp getCreatedAt() {
        return (Timestamp) get(3);
    }

    /**
     * Setter for <code>walk.action.user_id</code>.
     */
    public void setUserId(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>walk.action.user_id</code>.
     */
    public String getUserId() {
        return (String) get(4);
    }

    /**
     * Setter for <code>walk.action.user_name</code>.
     */
    public void setUserName(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>walk.action.user_name</code>.
     */
    public String getUserName() {
        return (String) get(5);
    }

    /**
     * Setter for <code>walk.action.user_email</code>.
     */
    public void setUserEmail(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>walk.action.user_email</code>.
     */
    public String getUserEmail() {
        return (String) get(6);
    }

    /**
     * Setter for <code>walk.action.type</code>.
     */
    public void setType(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>walk.action.type</code>.
     */
    public String getType() {
        return (String) get(7);
    }

    /**
     * Setter for <code>walk.action.before</code>.
     */
    public void setBefore(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>walk.action.before</code>.
     */
    public String getBefore() {
        return (String) get(8);
    }

    /**
     * Setter for <code>walk.action.after</code>.
     */
    public void setAfter(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>walk.action.after</code>.
     */
    public String getAfter() {
        return (String) get(9);
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<Long, Long, String, Timestamp, String, String, String, String, String, String> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<Long, Long, String, Timestamp, String, String, String, String, String, String> valuesRow() {
        return (Row10) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Action.ACTION.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field2() {
        return Action.ACTION.CLAIM_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Action.ACTION.PARTY_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field4() {
        return Action.ACTION.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Action.ACTION.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Action.ACTION.USER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Action.ACTION.USER_EMAIL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Action.ACTION.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Action.ACTION.BEFORE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return Action.ACTION.AFTER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value2() {
        return getClaimId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getPartyId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value4() {
        return getCreatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getUserName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getUserEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getBefore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getAfter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionRecord value2(Long value) {
        setClaimId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionRecord value3(String value) {
        setPartyId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionRecord value4(Timestamp value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionRecord value5(String value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionRecord value6(String value) {
        setUserName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionRecord value7(String value) {
        setUserEmail(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionRecord value8(String value) {
        setType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionRecord value9(String value) {
        setBefore(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionRecord value10(String value) {
        setAfter(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionRecord values(Long value1, Long value2, String value3, Timestamp value4, String value5, String value6, String value7, String value8, String value9, String value10) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ActionRecord
     */
    public ActionRecord() {
        super(Action.ACTION);
    }

    /**
     * Create a detached, initialised ActionRecord
     */
    public ActionRecord(Long id, Long claimId, String partyId, Timestamp createdAt, String userId, String userName, String userEmail, String type, String before, String after) {
        super(Action.ACTION);

        set(0, id);
        set(1, claimId);
        set(2, partyId);
        set(3, createdAt);
        set(4, userId);
        set(5, userName);
        set(6, userEmail);
        set(7, type);
        set(8, before);
        set(9, after);
    }
}
