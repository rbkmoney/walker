package com.rbkmoney.walker.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import com.rbkmoney.walker.dao.JiraDao;
import com.rbkmoney.walker.service.DescriptionBuilder;
import com.rbkmoney.walker.service.EnrichmentService;
import net.rcarz.jiraclient.JiraException;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PartyEventHandler implements Handler<StockEvent> {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private String path = "source_event.processing_event.payload.party_event";

    private Filter filter;

    public PartyEventHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }

    @Autowired
    JiraDao jiraDao;

    @Autowired
    DescriptionBuilder descriptionBuilder;

    @Autowired
    EnrichmentService enrichmentService;

    @Override
    public void handle(StockEvent value) {
        //Must not brake event order! - Its guaranted by event-stock library.
        Event event = value.getSourceEvent().getProcessingEvent();
        long eventId = event.getId();
        if (!event.getPayload().isSetPartyEvent()) {
            return;
        }

        String partyId = event.getSource().getParty();
        if (partyId.equals("39d17eca-0239-4ed8-8e32-dc78cf589135")) {
            log.error("Claim for party withId 39d17eca-0239-4ed8-8e32-dc78cf589135 will be ignored, eventId: {}", event.getId());
            return;
        }
        if (event.getPayload().getPartyEvent().isSetClaimCreated()) {
            log.info("Got ClaimCreated event with EventID: {}", eventId);
            if (event.getPayload().getPartyEvent().getClaimCreated().getStatus().isSetAccepted()) {
                log.info("Auto accepted claim with EventID: {} -  skipped.", eventId);
            } else {
                createIssue(event);
            }
        } else if (event.getPayload().getPartyEvent().isSetClaimStatusChanged()) {

            log.info("Got ClaimStatusChanged event with EventID: {}", eventId);
            ClaimStatusChanged claimStatusChanged = event.getPayload().getPartyEvent().getClaimStatusChanged();
            if (claimStatusChanged.getStatus().isSetRevoked()) {
                closeRevoked(eventId, partyId, claimStatusChanged);
            } else if (claimStatusChanged.getStatus().isSetAccepted()) {
                closeAccepted(eventId, partyId, claimStatusChanged);
            } else if (claimStatusChanged.getStatus().isSetDenied()) {
                closeDenied(eventId, partyId, claimStatusChanged);
            } else {
                log.error("Unsupported ClaimStatus changing for eventId : {}", eventId);
            }
        }
    }

    private void createIssue(Event processingEvent) {
        try {
            String partyEmail = enrichmentService.getPartyEmail(processingEvent.getSource().getParty());
            jiraDao.createIssue(
                    processingEvent.getId(),
                    processingEvent.getPayload().getPartyEvent().getClaimCreated().getId(),
                    processingEvent.getSource().getParty(),
                    partyEmail,
                    "Заявка " + partyEmail,
                    descriptionBuilder.buildDescription(processingEvent.getPayload().getPartyEvent().getClaimCreated()));
        } catch (JiraException e) {
            log.error("Cant Create issue with event id {}", processingEvent.getId(), e);
        } catch (TException e) {
            log.error("Enrichment remote call error {}", processingEvent.getId(), e);
        }
    }

    private void closeRevoked(long eventId, String partyId, ClaimStatusChanged claimStatusChanged) {
        try {
            jiraDao.closeRevokedIssue(
                    eventId,
                    claimStatusChanged.getId(),
                    partyId,
                    claimStatusChanged.getStatus().getRevoked().getReason());
        } catch (JiraException e) {
            log.error("Cant close Revoked claim with id {}", claimStatusChanged.getId(), e);
        }
    }

    private void closeAccepted(long eventId, String partyId, ClaimStatusChanged claimStatusChanged) {
        try {
            jiraDao.closeIssue(eventId, claimStatusChanged.getId(), partyId);
        } catch (JiraException e) {
            log.error("Cant close Accepted claim with id {}", claimStatusChanged.getId(), e);
        }
    }

    private void closeDenied(long eventId, String partyId, ClaimStatusChanged claimStatusChanged) {
        try {
            jiraDao.closeDeniedIssue(
                    eventId,
                    claimStatusChanged.getId(),
                    partyId,
                    claimStatusChanged.getStatus().getDenied().getReason());
        } catch (JiraException e) {
            log.error("Cant close Denied claim with id {}", claimStatusChanged.getId(), e);
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
