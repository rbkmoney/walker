package com.rbkmoney.walker.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Claim;
import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.payment_processing.PartyEvent;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import com.rbkmoney.walker.service.ActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.rbkmoney.walker.utils.ThriftConvertor.convertToPartyModificationUnit;
import static com.rbkmoney.walker.utils.ThriftConvertor.convertToJson;


@Component
public class PartyEventHandler implements Handler<StockEvent> {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private String path = "source_event.processing_event.payload.party_event";

    private Filter filter;

    public PartyEventHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path, new IsNullCondition().not()));
    }


    @Autowired
    private ClaimDao claimDao;

    @Autowired
    private ActionService actionService;


    @Override
    public void handle(StockEvent value) {
        try {
            //Must not brake event order! - Its guaranteed by event-stock library.
            long eventId = value.getSourceEvent().getProcessingEvent().getId();
            String partyId = value.getSourceEvent().getProcessingEvent().getSource().getParty();
            PartyEvent partyEvent = value.getSourceEvent().getProcessingEvent().getPayload().getPartyEvent();
            if (partyEvent.isSetClaimCreated()) {
                Claim claim = partyEvent.getClaimCreated();
                ClaimRecord claimRecord = new ClaimRecord();
                claimRecord.setId(claim.getId());
                claimRecord.setEventId(eventId);
                claimRecord.setRevision((long) claim.getRevision());
                claimRecord.setPartyId(partyId);

                PartyModificationUnit partyModificationUnit = convertToPartyModificationUnit(claim.getChangeset());
                claimRecord.setChanges(convertToJson(partyModificationUnit));
                claimDao.create(claimRecord);
                actionService.claimCreated(claim.getId(), claim.getChangeset(), partyId);
            } else if (partyEvent.isSetClaimUpdated()) {
                long claimId = partyEvent.getClaimStatusChanged().getId();
                ClaimRecord claimRecord = new ClaimRecord();
                claimRecord.setId(partyEvent.getClaimUpdated().getId());
                claimRecord.setEventId(eventId);
                claimRecord.setPartyId(partyId);
                //todo set revision if it come from event

                PartyModificationUnit partyModificationUnit = convertToPartyModificationUnit(partyEvent.getClaimUpdated().getChangeset());
                claimRecord.setChanges(convertToJson(partyModificationUnit));
                claimDao.update(claimRecord);
                actionService.claimUpdated(claimId, partyEvent.getClaimUpdated().getChangeset(), partyId);
            } else if (partyEvent.isSetClaimStatusChanged()) {
                long claimId = partyEvent.getClaimStatusChanged().getId();
                ClaimStatus status = partyEvent.getClaimStatusChanged().getStatus();
                claimDao.updateStatus(claimId, status);
                actionService.claimStatusChanged(claimId, status, partyId);
            } else if (partyEvent.isSetShopBlocking()) {
                log.info("Shop Blocking event {}", eventId);
            } else if (partyEvent.isSetShopSuspension()) {
                log.info("Shop Suspension event", eventId);
            } else {
                log.error("Unsupported event type: {}", partyEvent.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Filter getFilter() {
        return filter;
    }
}
