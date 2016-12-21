package com.rbkmoney.walker.handler;

import com.rbkmoney.damsel.domain.CategoryRef;
import com.rbkmoney.damsel.domain.ShopAccountSet;
import com.rbkmoney.damsel.domain.ShopDetails;
import com.rbkmoney.damsel.domain.ShopLocation;
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

import java.util.Optional;


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
        //Must not brake event order! - Its guaranted by event-stock library.
        Event event = value.getSourceEvent().getProcessingEvent();
        long eventId = event.getId();
        if (!event.getPayload().isSetPartyEvent()) {
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

    private void createIssue(Event processingEvent) {
        try {
            jiraDao.createIssue(
                    processingEvent.getId(),
                    processingEvent.getPayload().getPartyEvent().getClaimCreated().getId(),
                    processingEvent.getSource().getParty(),
                    "Заявка " + processingEvent.getSource().getParty(),
                    buildDescription(processingEvent.getPayload().getPartyEvent().getClaimCreated()));
        } catch (JiraException e) {
            log.error("Cant Create issue with event id {}", processingEvent.getId(), e);
        }
    }

    private String buildDescription(Claim claim) {
        String description = "";
        try {
            for (PartyModification modification : claim.getChangeset()) {
                if (modification.isSetShopCreation()) {
                    description += "\n \n h5. Операция: Создание магазина ";
                    description += "\n * Название: " + modification.getShopCreation().getDetails().getName();
                    description += "\n * Описание: " + modification.getShopCreation().getDetails().getDescription();
                    description += "\n * Местоположение: " + modification.getShopCreation().getDetails().getLocation();
                    description += "\n * Категория: " + modification.getShopCreation().getCategory().getId();
                    if (modification.getShopCreation().isSetContractId()) {
                        description += "\n Номер контракта: " + modification.getShopCreation().getContractId();
//                        description += "\n ID контрактора: " + modification.getShopCreation().getContract().getSystemContractor().getId();
//                        description += "\n Контракт заключен : " + modification.getShopCreation().getContract().getConcludedAt();
//                        description += "\n Действует с : " + modification.getShopCreation().getContract().getValidSince();
//                        description += "\n Действует до : " + modification.getShopCreation().getContract().getValidUntil();
//                        description += "\n Разорван : " + modification.getShopCreation().getContract().getTerminatedAt();
//                    }
                    } else if (modification.isSetShopModification()) {
                        description += "\n \n h5. Операция: Редактирование магазина ";
                        if (modification.getShopModification().getModification().isSetAccountsCreated()) {
                            ShopAccountSet accounts = modification.getShopModification().getModification().getAccountsCreated().getAccounts();
                            description += "\n * Созданы счета:";
                            description += "\n в валюте: " + accounts.getCurrency().getSymbolicCode();
                            description += "\n освновной счет: " + accounts.getGeneral();
                            description += "\n гарантийный счет: " + accounts.getGuarantee();
                        } else if (modification.getShopModification().getModification().isSetUpdate()) {
                            ShopUpdate update = modification.getShopModification().getModification().getUpdate();
                            description += "\n Изменен магазин : " + Optional.ofNullable(update.getDetails()).map(ShopDetails::getName).orElse("-");
                            description += "\n Описание : " + Optional.ofNullable(update.getDetails()).map(ShopDetails::getDescription).orElse("-");
                            description += "\n Местоположение : " + Optional.ofNullable(update.getDetails()).map(ShopDetails::getLocation).map(ShopLocation::getUrl).orElse("-");
                            description += "\n Категория : " + Optional.ofNullable(update.getCategory()).map(CategoryRef::getId).orElse(0);
                        } else {
                            description += "\n " + modification.getFieldValue().toString();
                        }
                    } else {
                        description += "\n " + modification.getFieldValue().toString();
                    }
                }
            }
        } catch (NullPointerException e) {
            log.error("Cant build correct description: {} ", claim.toString(), e);
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
