package com.rbkmoney.walker.config;

import com.rbkmoney.eventstock.client.*;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.eventstock.client.poll.PollingEventPublisherBuilder;
import com.rbkmoney.walker.dao.JiraDao;
import com.rbkmoney.walker.handler.Handler;
import com.rbkmoney.walker.handler.poller.EventStockErrorHandler;
import com.rbkmoney.walker.handler.poller.EventStockHandler;
import com.rbkmoney.walker.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Configuration
public class EventStockPollerConfig {

    @Value("${wk.pooling.url}")
    Resource bmUri;

    @Value("${wk.pooling.delay}")
    int pollDelay;

    @Value("${wk.pooling.maxPoolSize}")
    int maxPoolSize;

    @Autowired
    List<Handler> handlers;

    @Autowired
    JiraDao jiraDao;

    @Bean
    public EventPublisher eventPublisher() throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(bmUri.getURI())
                .withEventHandler(new EventStockHandler(handlers))
                .withErrorHandler(new EventStockErrorHandler())
                .withMaxPoolSize(maxPoolSize)
                .withPollDelay(pollDelay)
                .build();
    }

    @Bean
    public SubscriberConfig subscriberConfig() {
        return new DefaultSubscriberConfig(eventFilter());
    }

    public EventFilter eventFilter() {
        EventConstraint.EventIDRange eventIDRange = new EventConstraint.EventIDRange();
        Long lastEventId = jiraDao.getLastEventId();
        if (lastEventId != null) {
            eventIDRange.setFromExclusive(lastEventId);
        }
        return new EventFlowFilter(new EventConstraint(eventIDRange));
    }

}
