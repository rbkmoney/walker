package com.rbkmoney.walker.config;

import com.rbkmoney.walker.service.DescriptionBuilder;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @since 22.12.16
 **/
@Configuration
public class FreeMarkerConfig {

    private freemarker.template.Configuration cfg;

    @Bean
    public DescriptionBuilder descriptionBuilder() throws IOException {
        cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_25);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates/");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        return new DescriptionBuilder(cfg);
    }


}