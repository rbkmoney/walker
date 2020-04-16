package com.rbkmoney.walker.handler;

import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface EventHandler<T> {

    void handle(MachineEvent machineEvent, T event);

}
