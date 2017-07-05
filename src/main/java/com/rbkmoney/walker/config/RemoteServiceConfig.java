package com.rbkmoney.walker.config;

import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.woody.api.event.CompositeClientEventListener;
import com.rbkmoney.woody.thrift.impl.http.THPooledClientBuilder;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import com.rbkmoney.woody.thrift.impl.http.event.ClientEventLogListener;
import com.rbkmoney.woody.thrift.impl.http.event.HttpClientEventLogListener;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @since 14.02.17
 **/
@Configuration
public class RemoteServiceConfig {

    @Value("${hg.party.management.url}")
    private String PARTY_MANAGEMENT_SERVICE_URL;

    @Bean
    public PartyManagementSrv.Iface partyManagementSrv() throws IOException, URISyntaxException {
        return new THPooledClientBuilder()
                .withAddress(new URI(PARTY_MANAGEMENT_SERVICE_URL))
                .build(PartyManagementSrv.Iface.class);
    }

}
