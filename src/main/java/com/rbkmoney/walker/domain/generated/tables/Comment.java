/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.walker.domain.generated.tables;


import com.rbkmoney.walker.domain.generated.Keys;
import com.rbkmoney.walker.domain.generated.Walk;
import com.rbkmoney.walker.domain.generated.tables.records.CommentRecord;

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
public class Comment extends TableImpl<CommentRecord> {

    private static final long serialVersionUID = 1245352460;

    /**
     * The reference instance of <code>walk.comment</code>
     */
    public static final Comment COMMENT = new Comment();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CommentRecord> getRecordType() {
        return CommentRecord.class;
    }

    /**
     * The column <code>walk.comment.id</code>.
     */
    public final TableField<CommentRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('walk.comment_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>walk.comment.claim_id</code>.
     */
    public final TableField<CommentRecord, Long> CLAIM_ID = createField("claim_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>walk.comment.party_id</code>.
     */
    public final TableField<CommentRecord, String> PARTY_ID = createField("party_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>walk.comment.text</code>.
     */
    public final TableField<CommentRecord, String> TEXT = createField("text", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>walk.comment.created_at</code>.
     */
    public final TableField<CommentRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false).defaultValue(org.jooq.impl.DSL.field("now()", org.jooq.impl.SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>walk.comment.user_id</code>.
     */
    public final TableField<CommentRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>walk.comment.user_name</code>.
     */
    public final TableField<CommentRecord, String> USER_NAME = createField("user_name", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>walk.comment.email</code>.
     */
    public final TableField<CommentRecord, String> EMAIL = createField("email", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * Create a <code>walk.comment</code> table reference
     */
    public Comment() {
        this("comment", null);
    }

    /**
     * Create an aliased <code>walk.comment</code> table reference
     */
    public Comment(String alias) {
        this(alias, COMMENT);
    }

    private Comment(String alias, Table<CommentRecord> aliased) {
        this(alias, aliased, null);
    }

    private Comment(String alias, Table<CommentRecord> aliased, Field<?>[] parameters) {
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
    public Identity<CommentRecord, Long> getIdentity() {
        return Keys.IDENTITY_COMMENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Comment as(String alias) {
        return new Comment(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Comment rename(String name) {
        return new Comment(name, null);
    }
}