package com.rbkmoney.walker.dao;

import com.rbkmoney.walker.domain.generated.tables.records.LastEventIdRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import javax.sql.DataSource;
import static com.rbkmoney.walker.domain.generated.Tables.LAST_EVENT_ID;

/**
 * @since 15.03.17
 **/
public class LastEventDao extends NamedParameterJdbcDaoSupport {

    private DSLContext dslContext;

    Logger log = LoggerFactory.getLogger(this.getClass());

    public LastEventDao(DataSource ds) {
        setDataSource(ds);
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.POSTGRES);
        configuration.set(ds);
        this.dslContext = DSL.using(configuration);
    }

    public Long getLastEventId() {
        log.debug("Try to get last event id");
        Long lastEventId = getJdbcTemplate().queryForObject(dslContext
                .select(LAST_EVENT_ID.ID).from(LAST_EVENT_ID).toString(), Long.class);
        log.info("Got last eventID {} from db", lastEventId);
        return lastEventId;
    }

    public void update(long eventId) {
        UpdateSetMoreStep<LastEventIdRecord> update = dslContext.update(LAST_EVENT_ID)
                .set(LAST_EVENT_ID.ID, eventId);
        String sql = update.toString();
        getJdbcTemplate().update(sql);
    }
}
