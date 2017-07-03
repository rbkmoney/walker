package com.rbkmoney.walker.dao;


import com.bazaarvoice.jolt.JsonUtilImpl;
import com.bazaarvoice.jolt.JsonUtils;
import com.bazaarvoice.jolt.utils.JoltUtils;
import com.rbkmoney.geck.serializer.kit.object.ObjectHandler;
import com.rbkmoney.geck.serializer.kit.object.ObjectProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import com.rbkmoney.walker.domain.generated.tables.records.ActionRecord;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.walker.domain.generated.Tables.ACTION;
import static com.rbkmoney.walker.domain.generated.Tables.CLAIM;

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
