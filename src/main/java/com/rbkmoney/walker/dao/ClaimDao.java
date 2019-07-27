package com.rbkmoney.walker.dao;

import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.walker.ClaimSearchRequest;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static com.rbkmoney.walker.domain.generated.Tables.CLAIM;

public class ClaimDao extends NamedParameterJdbcDaoSupport {

    public static String WALKER_USER_ID = "0";
    private DSLContext dslContext;
    private String damselVersion;

    public ClaimDao(DataSource ds, String damselVersion) {
        this.damselVersion = damselVersion;
        setDataSource(ds);
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.POSTGRES);
        configuration.set(ds);
        this.dslContext = DSL.using(configuration);
    }

    public void create(ClaimRecord claimRecord) {
        if (StringUtils.isEmpty(claimRecord.getAssignedUserId())) {
            claimRecord.setAssignedUserId(WALKER_USER_ID);
        }
        String sql = dslContext.insertInto(CLAIM)
                .set(CLAIM.ID, claimRecord.getId())
                .set(CLAIM.PARTY_ID, claimRecord.getPartyId())
                .set(CLAIM.EVENT_ID, claimRecord.getEventId())
                .set(CLAIM.ASSIGNED_USER_ID, claimRecord.getAssignedUserId())
                .set(CLAIM.STATUS, claimRecord.getStatus())
                .set(CLAIM.PARTY_ID, claimRecord.getPartyId())
                .set(CLAIM.DESCRIPTION, claimRecord.getDescription())
                .set(CLAIM.REASON, claimRecord.getReason())
                .set(CLAIM.CHANGES, claimRecord.getChanges())
                .set(CLAIM.REVISION, claimRecord.getRevision())
                .set(CLAIM.UPDATED_AT, LocalDateTime.now())
                .set(CLAIM.DAMSEL_VERSION, damselVersion)
                .toString();
        getJdbcTemplate().update(sql);
    }

    public ClaimRecord get(String partyId, long claimId) {
        ClaimRecord claimRecord = dslContext.selectFrom(CLAIM)
                .where(CLAIM.ID.eq(claimId)).and(CLAIM.PARTY_ID.eq(partyId))
                .fetchOne();
        return claimRecord;
    }

    public void update(String partyId, Long claimId, Long eventId, Long revision, String changes) {
        UpdateSetMoreStep<ClaimRecord> update = dslContext.update(CLAIM)
                .set(CLAIM.EVENT_ID, eventId)
                .set(CLAIM.CHANGES, changes)
                .set(CLAIM.REVISION, revision)
                .set(CLAIM.UPDATED_AT, LocalDateTime.now())
                .set(CLAIM.DAMSEL_VERSION, damselVersion);
        String sql = update.where(CLAIM.ID.eq(claimId).and(CLAIM.PARTY_ID.eq(partyId))).toString();
        getJdbcTemplate().update(sql);
    }

    public void updateStatus(String partyId, long claimId, ClaimStatus claimStatus) {
        UpdateSetMoreStep<ClaimRecord> update = dslContext.update(CLAIM)
                .set(CLAIM.UPDATED_AT, LocalDateTime.now())
                .set(CLAIM.STATUS, getStatusName(claimStatus));

        if (claimStatus.isSetRevoked()) {
            update.set(CLAIM.REASON, claimStatus.getRevoked().getReason());
        }
        if (claimStatus.isSetDenied()) {
            update.set(CLAIM.REASON, claimStatus.getDenied().getReason());
        }
        String sql = update.where(CLAIM.ID.eq(claimId).and(CLAIM.PARTY_ID.eq(partyId))).toString();
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
        query.addLimit(100);
        if (request.getPartyId() != null) {
            query.addConditions(CLAIM.PARTY_ID.eq(request.getPartyId()));
        }
        if (request.getClaimId() != null && !request.getClaimId().isEmpty()) {
            query.addConditions(CLAIM.ID.in(request.getClaimId()));
        }
        if (request.getAssignedUserId() != null) {
            query.addConditions(CLAIM.ASSIGNED_USER_ID.eq(request.getAssignedUserId()));
        }
        if (request.getClaimStatus() != null) {
            query.addConditions(CLAIM.STATUS.eq(request.getClaimStatus()));
        }
        return query.fetch().into(ClaimRecord.class);
    }
}
