package com.rbkmoney.walker.dao;

import com.rbkmoney.damsel.walker.ClaimSearchRequest;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.util.List;
import java.util.Set;
import static com.rbkmoney.walker.domain.generated.Tables.CLAIM;

/**
 * @since 15.03.17
 **/
@Service
public class ClaimDao extends NamedParameterJdbcDaoSupport {

    public static String WALKER_USER = "WALKER";

    private DSLContext dslContext;

    public ClaimDao(DataSource ds) {
        setDataSource(ds);
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.POSTGRES);
        configuration.set(ds);
        this.dslContext = DSL.using(configuration);
    }

    public void create(ClaimRecord claimRecord) {
        if (StringUtils.isEmpty(claimRecord.getAssigned())) {
            claimRecord.setAssigned(WALKER_USER);
        }
        String sql = dslContext.insertInto(CLAIM, CLAIM.ID, CLAIM.EVENT_ID, CLAIM.ASSIGNED, CLAIM.CHANGES).
                values(claimRecord.getId(), claimRecord.getEventId(), claimRecord.getAssigned(), claimRecord.getChanges())
                .toString();
        getJdbcTemplate().update(sql);
    }

    public ClaimRecord get(long id) {
        ClaimRecord claimRecord = dslContext.selectFrom(CLAIM).where(CLAIM.ID.eq(id)).fetchOne();
        return claimRecord;
    }

    public void update(ClaimRecord claimRecord) {
        String sql = dslContext.update(CLAIM).set(CLAIM.EVENT_ID, claimRecord.getEventId())
                .set(CLAIM.ASSIGNED, claimRecord.getAssigned())
                .set(CLAIM.CHANGES, claimRecord.getChanges())
                .where(CLAIM.ID.eq(claimRecord.getId())).toString();
        getJdbcTemplate().update(sql);
    }

    public List<ClaimRecord> search(ClaimSearchRequest request) {
        //todo: "contains" - field does not work now.
        SelectQuery query = dslContext.selectQuery();
        query.addFrom(CLAIM);
        Set<Long> claimIDs = request.getClaimID();
        if (claimIDs != null) {
            query.addConditions(CLAIM.ID.in(claimIDs));
        }
        if (request.getAssigned() != null) {
            query.addConditions(CLAIM.ASSIGNED.eq(request.getAssigned()));
        }
        return query.fetch().into(ClaimRecord.class);
    }

    private void selectJsonExample() {
        String sql = "SELECT  * from walk.claim where changes->'contract_modification' ->> 'id' = '123';";
        Result<Record> result = dslContext.fetch(sql);
        List<ClaimRecord> list = result.into(ClaimRecord.class);
    }


}
