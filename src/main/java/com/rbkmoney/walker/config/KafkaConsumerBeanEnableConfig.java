package com.rbkmoney.walker.config;

import com.rbkmoney.walker.listener.PartyManagementListener;
import com.rbkmoney.walker.service.PartyManagementEventService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConsumerBeanEnableConfig {

    @Bean
    @ConditionalOnProperty(value = "info.single-instance-mode", havingValue = "false")
    public PartyManagementListener partyManagementListener(PartyManagementEventService partyManagementEventService) {
        return new PartyManagementListener(partyManagementEventService);
    }
}
