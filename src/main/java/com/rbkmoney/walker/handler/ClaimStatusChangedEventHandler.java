package com.rbkmoney.walker.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Claim;
import com.rbkmoney.damsel.payment_processing.ClaimStatusChanged;
import com.rbkmoney.damsel.payment_processing.PartyEvent;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import com.rbkmoney.walker.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClaimStatusChangedEventHandler implements Handler<StockEvent> {
    Logger log = LoggerFactory.getLogger(this.getClass());
    // tricky - filter pass must be at least 1 element longer then you want
    private String path = "source_event.processing_event.payload.party_event.claim_status_changed.id";

    private Filter filter;

    public ClaimStatusChangedEventHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }

    @Override
    public void handle(StockEvent value) {
        long eventId = value.getSourceEvent().getProcessingEvent().getId();

        PartyEvent partyEvent = value.getSourceEvent().getProcessingEvent().getPayload().getPartyEvent();
        if (partyEvent.isSetPartyCreated()) {
            log.info("isSetPartyCreated");
        } else if (partyEvent.isSetClaimCreated()) {
            log.info("isSetClaimCreated");
        } else if (partyEvent.isSetClaimStatusChanged()) {
            log.info("isSetClaimStatusChanged");
        }
        System.out.println("getClaimStatusChanged ACTIVEATED " + "eventId");
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
