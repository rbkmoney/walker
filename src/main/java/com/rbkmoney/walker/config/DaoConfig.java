package com.rbkmoney.walker.config;

import com.rbkmoney.walker.dao.ActionDao;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.dao.CommentDao;
import com.rbkmoney.walker.dao.LastEventDao;
import com.rbkmoney.walker.domain.generated.Walk;
import org.jooq.Schema;
import org.jooq.impl.SchemaImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

import static com.rbkmoney.walker.domain.generated.Walk.WALK;

@Configuration
public class DaoConfig {

    @Value("${info.damsel.version}")
    public String damselVersion;

    @Bean
    @DependsOn("dbInitializer")
    public ClaimDao claimDao(DataSource ds) {
        return new ClaimDao(ds, damselVersion);
    }

    @Bean
    @DependsOn("dbInitializer")
    public ActionDao actionDao(DataSource ds) {
        return new ActionDao(ds);
    }

    @Bean
    @DependsOn("dbInitializer")
    public CommentDao commentDao(DataSource ds) {
        return new CommentDao(ds);
    }

    @Bean
    @DependsOn("dbInitializer")
    public LastEventDao lastEventDao(DataSource ds) {
        return new LastEventDao(ds);
    }

    @Bean
    public Schema dbSchema() {
        return WALK;
    }
}
