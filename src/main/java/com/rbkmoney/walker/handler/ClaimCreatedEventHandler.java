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
public class ClaimCreatedEventHandler implements Handler<StockEvent> {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private String path = "source_event.processing_event.payload.party_event.claim_created.claim";

    private Filter filter;

    public ClaimCreatedEventHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }

    @Autowired
    JiraDao jiraDao;

    @Override
    public void handle(StockEvent value) {
        Event processingEvent = value.getSourceEvent().getProcessingEvent();
        log.info("Got Claim Created event {} ", processingEvent.getId());
        try {

            jiraDao.createIssue(
                    processingEvent.getId(),
                    processingEvent.getPayload().getPartyEvent().getClaimCreated().getClaim().getId(),
                    processingEvent.getSource().getParty(),
                    "Создана заявка",
                    buildDescription(processingEvent.getPayload().getPartyEvent().getClaimCreated()));
        } catch (JiraException e) {
            e.printStackTrace();
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


    @Override
    public Filter getFilter() {
        return filter;
    }
}
