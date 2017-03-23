package com.rbkmoney.walker.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Claim;
import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.payment_processing.PartyEvent;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.condition.IsNullCondition;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import com.rbkmoney.walker.service.ActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.rbkmoney.walker.service.ThriftObjectsConvertor.convertToPartyModificationUnit;
import static com.rbkmoney.walker.service.ThriftObjectsConvertor.convertToJson;


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

                PartyModificationUnit partyModificationUnit = convertToPartyModificationUnit(claim.getChangeset());
                claimRecord.setChanges(convertToJson(partyModificationUnit));
                claimDao.create(claimRecord);
                actionService.claimCreated(claim.getId(), claim.getChangeset(), partyId);
            }
            if (partyEvent.isSetClaimStatusChanged()) {
                long claimId = partyEvent.getClaimStatusChanged().getId();
                ClaimStatus status = partyEvent.getClaimStatusChanged().getStatus();
                claimDao.updateStatus(claimId, status);
                actionService.claimStatusChanged(claimId, status, partyId);
            }
            if(partyEvent.isSetClaimUpdated()){
                ClaimRecord claimRecord = new ClaimRecord();
                claimRecord.setId(partyEvent.getClaimUpdated().getId());
                claimRecord.setEventId(eventId);
                PartyModificationUnit partyModificationUnit = convertToPartyModificationUnit(partyEvent.getClaimUpdated().getChangeset());
                claimRecord.setChanges(convertToJson(partyModificationUnit));
                claimDao.update(claimRecord);
                //todo action
            }

            if (partyEvent.isSetShopBlocking()) {
                //todo:
            }
            if (partyEvent.isSetShopSuspention()) {
                //todo:
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
