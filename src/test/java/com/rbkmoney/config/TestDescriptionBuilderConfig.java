package com.rbkmoney.config;

import com.rbkmoney.walker.config.FreeMarkerConfig;
import com.rbkmoney.walker.config.JiraConfig;
import com.rbkmoney.walker.dao.JiraDao;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * @since 22.12.16
 **/
@Configuration
@Import({
        FreeMarkerConfig.class
})
@PropertySource("classpath:application.properties")

public class TestDescriptionBuilderConfig {
}
