/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.walker.domain.generated;


import javax.annotation.Generated;

import org.jooq.Sequence;
import org.jooq.impl.SequenceImpl;


/**
 * Convenience access to all sequences in walk
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Sequences {

    /**
     * The sequence <code>walk.action_id_seq</code>
     */
    public static final Sequence<Long> ACTION_ID_SEQ = new SequenceImpl<Long>("action_id_seq", Walk.WALK, org.jooq.impl.SQLDataType.BIGINT.nullable(false));
}
