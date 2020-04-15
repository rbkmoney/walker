package com.rbkmoney.walker.service;

import com.rbkmoney.machinegun.eventsink.MachineEvent;

import java.util.List;

public interface EventService {

    void handleEvents(List<MachineEvent> machineEvents);

}
