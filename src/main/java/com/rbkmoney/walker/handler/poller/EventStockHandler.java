package com.rbkmoney.walker.handler.poller;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.walker.handler.Handler;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class EventStockHandler implements EventHandler<StockEvent> {

    private final List<Handler> handlers;

    @Override
    public EventAction handle(StockEvent event, String subsKey) {
        for (Handler handler : handlers) {
            if (handler.accept(event)) {
                handler.handle(event);
            }
        }
        return EventAction.CONTINUE;
    }
}
