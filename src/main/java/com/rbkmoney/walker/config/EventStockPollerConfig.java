package com.rbkmoney.walker.config;

import com.rbkmoney.damsel.payment_processing.EventSinkSrv;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.eventstock.client.*;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.eventstock.client.poll.PollingEventPublisherBuilder;
import com.rbkmoney.walker.dao.JiraDao;
import com.rbkmoney.walker.handler.Handler;
import com.rbkmoney.walker.handler.poller.EventStockErrorHandler;
import com.rbkmoney.walker.handler.poller.EventStockHandler;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import net.rcarz.jiraclient.JiraException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.EnableRetry;
//import org.springframework.retry.annotation.EnableRetry;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Configuration
@EnableRetry
public class EventStockPollerConfig {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${bm.pooling.url}")
    Resource bmUri;

    @Value("${bm.pooling.delay}")
    int pollDelay;

    @Value("${bm.pooling.maxPoolSize}")
    int maxPoolSize;

    @Value("${hg.party.management.url}")
    private String PARTY_MANAGEMENT_SERVICE_URL;

    @Autowired
    List<Handler> handlers;

    @Autowired
    JiraDao jiraDao;

    @Bean
    public PartyManagementSrv.Iface partyManagementSrv() throws IOException, URISyntaxException {
        THSpawnClientBuilder clientBuilder = new THSpawnClientBuilder()
                .withHttpClient(HttpClientBuilder.create().build())
                .withAddress(new URI(PARTY_MANAGEMENT_SERVICE_URL));
        return clientBuilder.build(PartyManagementSrv.Iface.class);
    }

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
        Long lastEventId = null;
        try {
            lastEventId = jiraDao.getLastEventId();
        } catch (JiraException e) {
            log.error("Cant get last event id from jira", e);
        }
        if (lastEventId != null) {
            eventIDRange.setFromExclusive(lastEventId);
        }
        return new EventFlowFilter(new EventConstraint(eventIDRange));
    }

}
