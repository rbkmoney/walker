package com.rbkmoney;

import com.rbkmoney.extension.PostgresContainerExtension;
import com.rbkmoney.walker.WalkerApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(PostgresContainerExtension.class)
@SpringBootTest(classes = WalkerApplication.class, webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractIntegrationTest {

    @DynamicPropertySource
    static void connectionConfigs(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PostgresContainerExtension.POSTGRES::getJdbcUrl);
        registry.add("flyway.url", PostgresContainerExtension.POSTGRES::getJdbcUrl);
    }

}
