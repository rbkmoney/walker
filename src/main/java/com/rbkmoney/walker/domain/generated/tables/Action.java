/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.walker.domain.generated.tables;


import com.rbkmoney.walker.domain.generated.Keys;
import com.rbkmoney.walker.domain.generated.Walk;
import com.rbkmoney.walker.domain.generated.tables.records.ActionRecord;

import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;


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
public class Action extends TableImpl<ActionRecord> {

    private static final long serialVersionUID = -136360477;

    /**
     * The reference instance of <code>walk.action</code>
     */
    public static final Action ACTION = new Action();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ActionRecord> getRecordType() {
        return ActionRecord.class;
    }

    /**
     * The column <code>walk.action.id</code>.
     */
    public final TableField<ActionRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('walk.action_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>walk.action.claim_id</code>.
     */
    public final TableField<ActionRecord, Long> CLAIM_ID = createField("claim_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>walk.action.party_id</code>.
     */
    public final TableField<ActionRecord, String> PARTY_ID = createField("party_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>walk.action.created_at</code>.
     */
    public final TableField<ActionRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false).defaultValue(org.jooq.impl.DSL.field("now()", org.jooq.impl.SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>walk.action.user_id</code>.
     */
    public final TableField<ActionRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>walk.action.user_name</code>.
     */
    public final TableField<ActionRecord, String> USER_NAME = createField("user_name", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>walk.action.user_email</code>.
     */
    public final TableField<ActionRecord, String> USER_EMAIL = createField("user_email", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>walk.action.type</code>.
     */
    public final TableField<ActionRecord, String> TYPE = createField("type", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>walk.action.before</code>.
     */
    public final TableField<ActionRecord, String> BEFORE = createField("before", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>walk.action.after</code>.
     */
    public final TableField<ActionRecord, String> AFTER = createField("after", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * Create a <code>walk.action</code> table reference
     */
    public Action() {
        this("action", null);
    }

    /**
     * Create an aliased <code>walk.action</code> table reference
     */
    public Action(String alias) {
        this(alias, ACTION);
    }

    private Action(String alias, Table<ActionRecord> aliased) {
        this(alias, aliased, null);
    }

    private Action(String alias, Table<ActionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Walk.WALK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<ActionRecord, Long> getIdentity() {
        return Keys.IDENTITY_ACTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action as(String alias) {
        return new Action(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Action rename(String name) {
        return new Action(name, null);
    }
}
