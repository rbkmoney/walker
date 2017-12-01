/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.walker.domain.generated;


import com.rbkmoney.walker.domain.generated.tables.Action;
import com.rbkmoney.walker.domain.generated.tables.Claim;
import com.rbkmoney.walker.domain.generated.tables.Comment;
import com.rbkmoney.walker.domain.generated.tables.LastEventId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


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
public class Walk extends SchemaImpl {

    private static final long serialVersionUID = 226960240;

    /**
     * The reference instance of <code>walk</code>
     */
    public static final Walk WALK = new Walk();

    /**
     * The table <code>walk.action</code>.
     */
    public final Action ACTION = com.rbkmoney.walker.domain.generated.tables.Action.ACTION;

    /**
     * The table <code>walk.claim</code>.
     */
    public final Claim CLAIM = com.rbkmoney.walker.domain.generated.tables.Claim.CLAIM;

    /**
     * The table <code>walk.comment</code>.
     */
    public final Comment COMMENT = com.rbkmoney.walker.domain.generated.tables.Comment.COMMENT;

    /**
     * The table <code>walk.last_event_id</code>.
     */
    public final LastEventId LAST_EVENT_ID = com.rbkmoney.walker.domain.generated.tables.LastEventId.LAST_EVENT_ID;

    /**
     * No further instances allowed
     */
    private Walk() {
        super("walk", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        List result = new ArrayList();
        result.addAll(getSequences0());
        return result;
    }

    private final List<Sequence<?>> getSequences0() {
        return Arrays.<Sequence<?>>asList(
            Sequences.ACTION_ID_SEQ,
            Sequences.COMMENT_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Action.ACTION,
            Claim.CLAIM,
            Comment.COMMENT,
            LastEventId.LAST_EVENT_ID);
    }
}
