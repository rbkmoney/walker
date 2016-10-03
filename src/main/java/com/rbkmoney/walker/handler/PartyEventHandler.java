package com.rbkmoney.walker.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import com.rbkmoney.walker.dao.JiraDao;
import net.rcarz.jiraclient.JiraException;
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

    @Override
    public void handle(StockEvent value) {
        synchronized (this) { //Must not brake event order!
            Event event = value.getSourceEvent().getProcessingEvent();
            long eventId = event.getId();

            if (event.getPayload().getPartyEvent().isSetClaimCreated()) {
                log.info("Got ClaimCreated event with EventID: {}", eventId);
                if (event.getPayload().getPartyEvent().getClaimCreated().getClaim().getStatus().isSetAccepted()) {
                    log.info("Auto accepted claim with EventID: {} -  skipped.", eventId);
                } else {
                    createIssue(event);
                }
            } else if (event.getPayload().getPartyEvent().isSetClaimStatusChanged()) {
                log.info("Got ClaimStatusChanged event with EventID: {}", eventId);
                ClaimStatusChanged claimStatusChanged = event.getPayload().getPartyEvent().getClaimStatusChanged();
                if (claimStatusChanged.getStatus().isSetRevoked()) {
                    closeRevoked(eventId, claimStatusChanged);
                } else if (claimStatusChanged.getStatus().isSetAccepted()) {
                    closeAccepted(eventId, claimStatusChanged);
                } else if (claimStatusChanged.getStatus().isSetDenied()) {
                    closeDenied(eventId, claimStatusChanged);
                } else {
                    log.error("Unsupported ClaimStatus changing for eventId : {}", eventId);
                }
            }
        }
    }

    private void createIssue(Event processingEvent) {
        try {
            jiraDao.createIssue(
                    processingEvent.getId(),
                    processingEvent.getPayload().getPartyEvent().getClaimCreated().getClaim().getId(),
                    processingEvent.getSource().getParty(),
                    "Создана заявка",
                    buildDescription(processingEvent.getPayload().getPartyEvent().getClaimCreated()));
        } catch (JiraException e) {
            log.error("Cant Create issue with event id {}", processingEvent.getId(), e);
        }
    }

    private String buildDescription(ClaimCreated claimCreated) {
        String description = "Операция :";
        for (PartyModification modification : claimCreated.getClaim().getChangeset()) {
            if (modification.isSetShopCreation()) {
                description += " Создание магазина";
                description += "\n Название: " + modification.getShopCreation().getDetails().getName();
                description += "\n Описание: " + modification.getShopCreation().getDetails().getDescription();
                description += "\n Местоположение: " + modification.getShopCreation().getDetails().getLocation();
                description += "\n Категория: " + modification.getShopCreation().getCategory().getData().getName();
                description += "\n Описание категории: " + modification.getShopCreation().getCategory().getData().getDescription();
            } else {
                description += "\n " + modification.getFieldValue().toString();
            }
        }
        return description;
    }

    private void closeRevoked(long eventId, ClaimStatusChanged claimStatusChanged) {
        try {
            jiraDao.closeRevokedIssue(
                    eventId,
                    claimStatusChanged.getId(),
                    claimStatusChanged.getStatus().getRevoked().getReason());
        } catch (JiraException e) {
            log.error("Cant close Revoked claim with id {}", claimStatusChanged.getId(), e);
        }
    }

    private void closeAccepted(long eventId, ClaimStatusChanged claimStatusChanged) {
        try {
            jiraDao.closeIssue(eventId, claimStatusChanged.getId());
        } catch (JiraException e) {
            log.error("Cant close Accepted claim with id {}", claimStatusChanged.getId(), e);
        }
    }

    private void closeDenied(long eventId, ClaimStatusChanged claimStatusChanged) {
        try {
            jiraDao.closeDeniedIssue(
                    eventId,
                    claimStatusChanged.getId(),
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
