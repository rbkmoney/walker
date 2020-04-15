package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.payment_processing.PartyEventData;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import com.rbkmoney.walker.handler.EventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyManagementEventService implements EventService {

    private final MachineEventParser<PartyEventData> parser;

    private final EventHandler<PartyEventData> partyEventHandler;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> machineEvents) {
        for (MachineEvent machineEvent : machineEvents) {
            PartyEventData eventPayload = parser.parse(machineEvent);
            partyEventHandler.handle(machineEvent, eventPayload);
        }
    }

}
