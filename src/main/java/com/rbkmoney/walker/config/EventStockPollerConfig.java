package com.rbkmoney.walker.config;

import com.rbkmoney.eventstock.client.*;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.eventstock.client.poll.PollingEventPublisherBuilder;
import com.rbkmoney.walker.dao.LastEventDao;
import com.rbkmoney.walker.handler.Handler;
import com.rbkmoney.walker.handler.poller.EventStockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${bm.pooling.url}")
    Resource bmUri;

    @Value("${bm.pooling.delay}")
    int pollDelay;

    @Value("${bm.pooling.maxPoolSize}")
    int maxPoolSize;

    @Autowired
    List<Handler> handlers;

    @Autowired
    LastEventDao lastEventDao;

    @Bean
    public EventPublisher eventPublisher() throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(bmUri.getURI())
                .withEventHandler(new EventStockHandler(handlers))
                .withMaxPoolSize(maxPoolSize)
                .withPollDelay(pollDelay)
                .build();
    }

    @Bean
    public SubscriberConfig subscriberConfig() {
        return new DefaultSubscriberConfig(buildEventFilter());
    }

    public EventFilter buildEventFilter() {
        EventConstraint.EventIDRange eventIDRange = new EventConstraint.EventIDRange();
        Long lastEventId = null;
        try {
            lastEventId = lastEventDao.getLastEventId();
        } catch (Exception e) {
            log.error("Cant get last event id", e);
        }
        if (lastEventId != null) {
            eventIDRange.setFromExclusive(lastEventId);
        }
        return new EventFlowFilter(new EventConstraint(eventIDRange));
    }

}
