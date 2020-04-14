package com.rbkmoney.walker.handler;

import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyEventData;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.walker.handler.party.PartyChangeEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartyEventHandler implements EventHandler<PartyEventData> {

    private final PartyChangeEventHandler claimCreatedHandler;

    private final PartyChangeEventHandler claimStatusChangedHandler;

    private final PartyChangeEventHandler claimUpdatedHandler;

    @Override
    public void handle(MachineEvent machineEvent, PartyEventData event) {
        try {
            long eventId = machineEvent.getEventId();
            String partyId = machineEvent.getSourceId();
            List<PartyChange> partyChanges = event.getChanges();
            for (PartyChange partyChange : partyChanges) {
                if (partyChange.isSetClaimCreated()) {
                    claimCreatedHandler.handleEvent(partyChange, eventId, partyId);
                } else if (partyChange.isSetClaimUpdated()) {
                    claimUpdatedHandler.handleEvent(partyChange, eventId, partyId);
                } else if (partyChange.isSetClaimStatusChanged()) {
                    claimStatusChangedHandler.handleEvent(partyChange, eventId, partyId);
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

}
