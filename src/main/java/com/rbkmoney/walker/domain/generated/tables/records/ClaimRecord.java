/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.walker.domain.generated.tables.records;


import com.rbkmoney.walker.domain.generated.tables.Claim;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


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
public class ClaimRecord extends UpdatableRecordImpl<ClaimRecord> implements Record4<Long, Long, String, Object> {

    private static final long serialVersionUID = -1369015140;

    /**
     * Setter for <code>walk.claim.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>walk.claim.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>walk.claim.event_id</code>.
     */
    public void setEventId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>walk.claim.event_id</code>.
     */
    public Long getEventId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>walk.claim.assigned</code>.
     */
    public void setAssigned(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>walk.claim.assigned</code>.
     */
    public String getAssigned() {
        return (String) get(2);
    }

    /**
     * Setter for <code>walk.claim.changes</code>.
     */
    public void setChanges(Object value) {
        set(3, value);
    }

    /**
     * Getter for <code>walk.claim.changes</code>.
     */
    public Object getChanges() {
        return (Object) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<Long, Long, String, Object> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<Long, Long, String, Object> valuesRow() {
        return (Row4) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Claim.CLAIM.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field2() {
        return Claim.CLAIM.EVENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Claim.CLAIM.ASSIGNED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Object> field4() {
        return Claim.CLAIM.CHANGES;
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
        return getEventId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getAssigned();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object value4() {
        return getChanges();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClaimRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClaimRecord value2(Long value) {
        setEventId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClaimRecord value3(String value) {
        setAssigned(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClaimRecord value4(Object value) {
        setChanges(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClaimRecord values(Long value1, Long value2, String value3, Object value4) {
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
     * Create a detached ClaimRecord
     */
    public ClaimRecord() {
        super(Claim.CLAIM);
    }

    /**
     * Create a detached, initialised ClaimRecord
     */
    public ClaimRecord(Long id, Long eventId, String assigned, Object changes) {
        super(Claim.CLAIM);

        set(0, id);
        set(1, eventId);
        set(2, assigned);
        set(3, changes);
    }
}
