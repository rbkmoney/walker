package com.rbkmoney.config;

import com.rbkmoney.mock.EnrichmentServiceMock;
import com.rbkmoney.walker.config.FreeMarkerConfig;
import com.rbkmoney.walker.config.JiraConfig;
import com.rbkmoney.walker.dao.JiraDao;
import com.rbkmoney.walker.service.EnrichmentService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;
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

    @Mock
    EnrichmentService enrichmentService;

    public TestDescriptionBuilderConfig() {
        MockitoAnnotations.initMocks(this);
    }

    @Bean
    public EnrichmentService getEnrichmentService() {
        return new EnrichmentServiceMock();
    }


}
