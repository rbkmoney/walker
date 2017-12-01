package com.rbkmoney.walker.handler.poller;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.walker.handler.Handler;

import java.util.List;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public class EventStockHandler implements EventHandler<StockEvent> {

    List<Handler> handlers;

    public EventStockHandler(List<Handler> handlers) {
        this.handlers = handlers;
    }

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
