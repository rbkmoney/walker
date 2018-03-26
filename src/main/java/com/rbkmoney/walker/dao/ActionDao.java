package com.rbkmoney.walker.dao;


import com.rbkmoney.walker.domain.generated.tables.records.ActionRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.walker.domain.generated.Tables.ACTION;

/**
 * @since 15.03.17
 **/
public class ActionDao extends NamedParameterJdbcDaoSupport {

    private DSLContext dslContext;

    public ActionDao(DataSource ds) {
        setDataSource(ds);
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.POSTGRES);
        configuration.set(ds);
        this.dslContext = DSL.using(configuration);
    }

    public void add(ActionRecord actionRecord) {
        dslContext.insertInto(ACTION).set(actionRecord).execute();
    }

    public List<ActionRecord> getActions(String partyId, Long claimId) {
        Result<ActionRecord> fetch = dslContext
                .selectFrom(ACTION)
                .where(ACTION.CLAIM_ID.eq(claimId).and(ACTION.PARTY_ID.eq(partyId)))
                .fetch();
        return fetch.stream().collect(Collectors.toList());
    }

}
