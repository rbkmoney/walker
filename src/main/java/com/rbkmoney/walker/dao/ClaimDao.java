package com.rbkmoney.walker.dao;

import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.walker.ClaimSearchRequest;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.util.List;
import java.util.Set;

import static com.rbkmoney.walker.domain.generated.Tables.CLAIM;
import static org.jooq.impl.DSL.max;

/**
 * @since 15.03.17
 **/
public class ClaimDao extends NamedParameterJdbcDaoSupport {

    public static String WALKER_USER_ID = "0";

    private DSLContext dslContext;

    Logger log = LoggerFactory.getLogger(this.getClass());

    public ClaimDao(DataSource ds) {
        setDataSource(ds);
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.POSTGRES);
        configuration.set(ds);
        this.dslContext = DSL.using(configuration);
    }

    public Long getLastEventId() {
        log.debug("Try to get last event id");
        Long lastEventId = getJdbcTemplate().queryForObject(dslContext
                .select(max(CLAIM.EVENT_ID)).from(CLAIM).toString(), Long.class);
        log.info("Got last eventID {} from db", lastEventId);
        return lastEventId;
    }

    public void create(ClaimRecord claimRecord) {
        if (StringUtils.isEmpty(claimRecord.getAssignedUserId())) {
            claimRecord.setAssignedUserId(WALKER_USER_ID);
        }
        String sql = dslContext.insertInto(CLAIM)
                .set(CLAIM.ID, claimRecord.getId())
                .set(CLAIM.EVENT_ID, claimRecord.getEventId())
                .set(CLAIM.ASSIGNED_USER_ID, claimRecord.getAssignedUserId())
                .set(CLAIM.STATUS, claimRecord.getStatus())
                .set(CLAIM.DESCRIPTION, claimRecord.getDescription())
                .set(CLAIM.REASON, claimRecord.getReason())
                .set(CLAIM.CHANGES, claimRecord.getChanges())
                .set(CLAIM.REVISION, claimRecord.getRevision())
                .toString();
        getJdbcTemplate().update(sql);
    }

    public ClaimRecord get(long id) {
        ClaimRecord claimRecord = dslContext.selectFrom(CLAIM).where(CLAIM.ID.eq(id)).fetchOne();
        return claimRecord;
    }

    public void update(ClaimRecord claimRecord) {
        UpdateSetMoreStep<ClaimRecord> update = dslContext.update(CLAIM)
                .set(CLAIM.EVENT_ID, claimRecord.getEventId())
                .set(CLAIM.ASSIGNED_USER_ID, claimRecord.getAssignedUserId())
                .set(CLAIM.STATUS, claimRecord.getStatus())
                .set(CLAIM.CHANGES, claimRecord.getChanges())
                .set(CLAIM.DESCRIPTION, claimRecord.getDescription())
                .set(CLAIM.REVISION, claimRecord.getRevision());
        if (StringUtils.isEmpty(claimRecord.getReason())) {
            update.set(CLAIM.REASON, claimRecord.getReason());
        }
        String sql = update.where(CLAIM.ID.eq(claimRecord.getId())).toString();
        getJdbcTemplate().update(sql);
    }

    public void updateStatus(long claimId, ClaimStatus claimStatus) {
        UpdateSetMoreStep<ClaimRecord> update = dslContext.update(CLAIM)
                .set(CLAIM.STATUS, getStatusName(claimStatus));

        if (claimStatus.isSetRevoked()) {
            update.set(CLAIM.REASON, claimStatus.getRevoked().getReason());
        }
        if (claimStatus.isSetDenied()) {
            update.set(CLAIM.REASON, claimStatus.getDenied().getReason());
        }
        String sql = update.where(CLAIM.ID.eq(claimId)).toString();
        getJdbcTemplate().update(sql);
    }

    public static String getStatusName(ClaimStatus claimStatus) {
        if (claimStatus.isSetAccepted()) {
            return "accepted";
        }
        if (claimStatus.isSetDenied()) {
            return "denied";
        }
        if (claimStatus.isSetRevoked()) {
            return "revoked";
        }
        if (claimStatus.isSetPending()) {
            return "pending";
        } else {
            return "unknown";
        }
    }


    public List<ClaimRecord> search(ClaimSearchRequest request) {
        //todo: "contains" - field does not work now.
        SelectQuery query = dslContext.selectQuery();
        query.addFrom(CLAIM);
        Set<Long> claimIDs = request.getClaimId();
        if (claimIDs != null) {
            query.addConditions(CLAIM.ID.in(claimIDs));
        }
        if (request.getAssignedUserId() != null) {
            query.addConditions(CLAIM.ASSIGNED_USER_ID.eq(request.getAssignedUserId()));
        }
        return query.fetch().into(ClaimRecord.class);
    }

    private void selectJsonExample() {
        String sql = "SELECT  * from walk.claim where changes->'contract_modification' ->> 'id' = '123';";
        Result<Record> result = dslContext.fetch(sql);
        List<ClaimRecord> list = result.into(ClaimRecord.class);
    }


}