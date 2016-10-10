package com.rbkmoney.config;

import com.rbkmoney.walker.config.JiraConfig;
import com.rbkmoney.walker.dao.JiraDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@Configuration
@Import({
        JiraDao.class,
        JiraConfig.class
})
@PropertySource("classpath:application.properties")
public class TestJiraConfig {


}
