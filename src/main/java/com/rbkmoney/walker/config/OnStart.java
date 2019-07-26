package com.rbkmoney.walker.config;

import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.SubscriberConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnStart implements ApplicationListener<ApplicationReadyEvent> {

    private final EventPublisher eventPublisher;
    private final SubscriberConfig subscriberConfig;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        eventPublisher.subscribe(subscriberConfig);
    }

}
