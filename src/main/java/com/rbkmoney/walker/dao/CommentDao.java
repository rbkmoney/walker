package com.rbkmoney.walker.dao;

import com.rbkmoney.walker.domain.generated.tables.records.CommentRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.SelectQuery;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

import static com.rbkmoney.walker.domain.generated.Tables.COMMENT;

/**
 * @since 15.03.17
 **/
public class CommentDao extends NamedParameterJdbcDaoSupport {

    private DSLContext dslContext;

    public CommentDao(DataSource ds) {
        setDataSource(ds);
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.POSTGRES);
        configuration.set(ds);
        this.dslContext = DSL.using(configuration);
    }

    public void add(CommentRecord commentRecord) {
        if (commentRecord.getCreatedAt() == null) {
            commentRecord.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        String sql = dslContext.insertInto(COMMENT)
                .set(COMMENT.CLAIM_ID, commentRecord.getClaimId())
                .set(COMMENT.PARTY_ID, commentRecord.getPartyId())
                .set(COMMENT.CREATED_AT, commentRecord.getCreatedAt())
                .set(COMMENT.EMAIL, commentRecord.getEmail())
                .set(COMMENT.USER_NAME, commentRecord.getUserName())
                .set(COMMENT.USER_ID, commentRecord.getUserId())
                .set(COMMENT.TEXT, commentRecord.getText())
                .toString();
        getJdbcTemplate().update(sql);
    }

    public List<CommentRecord> getComments(Long claimId) {
        SelectQuery query = dslContext.selectQuery();
        query.addFrom(COMMENT);
        query.addConditions(COMMENT.CLAIM_ID.eq(claimId));
        return query.fetch().into(CommentRecord.class);
    }
}
