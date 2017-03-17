package com.rbkmoney.walker.handler;

import com.bazaarvoice.jolt.JsonUtilImpl;
import com.bazaarvoice.jolt.JsonUtils;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Claim;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyModification;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.geck.serializer.kit.object.ObjectHandler;
import com.rbkmoney.geck.serializer.kit.object.ObjectProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


@Component
public class PartyEventHandler implements Handler<StockEvent> {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private String path = "source_event.processing_event.payload.party_event";

    private Filter filter;

    public PartyEventHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }


    @Autowired
    ClaimDao claimDao;

    @Override
    public void handle(StockEvent value) {
        try {
            //Must not brake event order! - Its guaranteed by event-stock library.
            Event event = value.getSourceEvent().getProcessingEvent();
            long eventId = event.getId();
            if (event.getPayload().getPartyEvent().isSetClaimCreated()) {
                Claim claim = event.getPayload().getPartyEvent().getClaimCreated();
                ClaimRecord claimRecord = new ClaimRecord();

                claimRecord.setId(claim.getId());
                claimRecord.setEventid(eventId);

                PartyModificationUnit partyModificationUnit = convertToPartyModificationUnit(claim.getChangeset());
                claimRecord.setChanges(toJson(partyModificationUnit));

                claimDao.create(claimRecord);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJson(PartyModificationUnit partyModificationUnit) throws IOException {
        Object object = new TBaseProcessor().process(partyModificationUnit, new ObjectHandler());
        return JsonUtils.toJsonString(object);
    }

    public PartyModificationUnit convertToPartyModificationUnit(List<PartyModification> hgModifications) throws IOException {
        LinkedList<com.rbkmoney.damsel.walker.PartyModification> walkerPartyModificationList = new LinkedList<>();
        for (PartyModification hgModification : hgModifications) {
            com.rbkmoney.damsel.walker.PartyModification partyModification = convertToWalkerModification(hgModification);
            walkerPartyModificationList.add(partyModification);
        }
        PartyModificationUnit partyModificationUnit = new PartyModificationUnit();
        partyModificationUnit.setModifications(walkerPartyModificationList);
        return partyModificationUnit;
    }

    /**
     * Convert from Payment_processing thrift object to Walker thrift representation
     */
    public com.rbkmoney.damsel.walker.PartyModification convertToWalkerModification(PartyModification hgModification) throws IOException {
        Object hgModifObj = new TBaseProcessor().process(hgModification, new ObjectHandler());
        String hgJson = JsonUtils.toJsonString(hgModifObj);
        Object objFromJson = new JsonUtilImpl().jsonToObject(hgJson);
        return new ObjectProcessor()
                .process(objFromJson, new TBaseHandler<>(com.rbkmoney.damsel.walker.PartyModification.class));
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
