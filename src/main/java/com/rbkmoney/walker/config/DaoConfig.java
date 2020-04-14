package com.rbkmoney.walker.config;

import com.rbkmoney.walker.dao.ActionDao;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.dao.CommentDao;
import org.jooq.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static com.rbkmoney.walker.domain.generated.Walk.WALK;

@Configuration
public class DaoConfig {

    @Value("${info.damsel.version}")
    public String damselVersion;

    @Bean
    public ClaimDao claimDao(DataSource ds) {
        return new ClaimDao(ds, damselVersion);
    }

    @Bean
    public ActionDao actionDao(DataSource ds) {
        return new ActionDao(ds);
    }

    @Bean
    public CommentDao commentDao(DataSource ds) {
        return new CommentDao(ds);
    }

    @Bean
    public Schema dbSchema() {
        return WALK;
    }

}
