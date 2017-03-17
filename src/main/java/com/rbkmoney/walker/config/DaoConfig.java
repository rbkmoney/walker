package com.rbkmoney.walker.config;

import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.domain.generated.Walk;
import org.jooq.Schema;
import org.jooq.impl.SchemaImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

import static com.rbkmoney.walker.domain.generated.Walk.WALK;

@Configuration
public class DaoConfig {

    @Bean
    @DependsOn("dbInitializer")
    public ClaimDao statisticsDao(DataSource ds) {
        return new ClaimDao(ds);
    }

    @Bean
    public Schema dbSchema() {
        return WALK;
    }
}
