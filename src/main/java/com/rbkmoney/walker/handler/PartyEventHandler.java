package com.rbkmoney.walker.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Claim;
import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.payment_processing.PartyChange;
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
import java.util.List;

import static com.rbkmoney.walker.dao.ClaimDao.getStatusName;
import static com.rbkmoney.walker.utils.ThriftConvertor.*;


@Component
public class PartyEventHandler implements Handler<StockEvent> {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private String path = "source_event.processing_event.payload.party_changes";

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
            String partyId = value.getSourceEvent().getProcessingEvent().getSource().getPartyId();
            List<PartyChange> partyChanges = value.getSourceEvent().getProcessingEvent().getPayload().getPartyChanges();
            for (PartyChange partyChange : partyChanges) {
                if (partyChange.isSetClaimCreated()) {
                    Claim claim = partyChange.getClaimCreated();
                    log.info("Got claim created PartyId: {}, ClaimId: {}", partyId, claim.getId());
                    ClaimRecord claimRecord = new ClaimRecord();
                    claimRecord.setId(claim.getId());
                    claimRecord.setEventId(eventId);
                    claimRecord.setRevision((long) claim.getRevision());
                    claimRecord.setPartyId(partyId);
                    claimRecord.setStatus(getStatusName(claim.getStatus()));
                    claimRecord.setDescription("Заявка " + claim.getId() + " от участника с PartyId " + partyId);

                    PartyModificationUnit partyModificationUnit = convertToPartyModificationUnit(claim.getChangeset());
                    claimRecord.setChanges(convertToJson(partyModificationUnit));
                    claimDao.create(claimRecord);
                    actionService.claimCreated(partyId, claim.getId(), claim.getChangeset(), "event");
                } else if (partyChange.isSetClaimUpdated()) {
                    long claimId = partyChange.getClaimUpdated().getId();
                    log.info("Got claim updated PartyId: {}, ClaimId: {}", partyId, claimId);
                    ClaimRecord claimRecord = claimDao.get(partyId, claimId);
                    PartyModificationUnit partyModificationUnit = fromJsonPartyModificationUnit(String.valueOf(claimRecord.getChanges()));

                    partyModificationUnit.getModifications().addAll(
                            convertToPartyModificationUnit(partyChange.getClaimUpdated().getChangeset()).getModifications());

                    Long revision = (long) partyChange.getClaimUpdated().getRevision();

                    claimDao.update(partyId, claimId, eventId, revision, convertToJson(partyModificationUnit));
                    actionService.claimUpdated(partyId, claimId, partyChange.getClaimUpdated().getChangeset(), "event");
                } else if (partyChange.isSetClaimStatusChanged()) {
                    long claimId = partyChange.getClaimStatusChanged().getId();
                    ClaimStatus status = partyChange.getClaimStatusChanged().getStatus();
                    log.info("Got claim status changed Status: {}, PartyId: {}, ClaimId: {}", status.toString(), partyId, claimId);
                    claimDao.updateStatus(partyId, claimId, status);
                    actionService.claimStatusChanged(partyId, claimId, status, "event");
                } else if (partyChange.isSetShopBlocking()) {
                    log.info("Shop Blocking event {}", eventId);
                } else if (partyChange.isSetShopSuspension()) {
                    log.info("Shop Suspension event", eventId);
                }
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
