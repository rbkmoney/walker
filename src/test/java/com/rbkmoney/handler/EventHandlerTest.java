package com.rbkmoney.handler;

import com.rbkmoney.AbstractIntegrationTest;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.PartyEvent;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.walker.handler.PartyEventHandler;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;


/**
 * @since 30.06.17
 **/
public class EventHandlerTest extends AbstractIntegrationTest {

    @Autowired
    PartyEventHandler partyEventHandler;

    String PARTY_ID = "test-party-id";

    @Test
    public void testCreateEvent() throws IOException {
//        partyEventHandler.handle(buildStockEvent());

    }


    public StockEvent buildStockEvent() throws IOException {
        StockEvent emptyStockEvent = new StockEvent();
        StockEvent stockEvent = new MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(emptyStockEvent, new TBaseHandler<>(StockEvent.class));
        PartyEvent emptyPartyEvent = new PartyEvent();
        PartyEvent partyEvent = new MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(emptyPartyEvent, new TBaseHandler<>(PartyEvent.class));

        stockEvent.getSourceEvent().getProcessingEvent().getPayload().setPartyEvent(partyEvent);

        return stockEvent;
    }
}
