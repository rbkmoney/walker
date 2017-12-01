package com.rbkmoney.walker.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.walker.dao.LastEventDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AnyEventHandler implements Handler<StockEvent> {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LastEventDao lastEventDao;

    @Override
    public boolean accept(StockEvent value) {
        return true;
    }

    @Override
    public void handle(StockEvent value) {
        long eventId = -1;
        if (value.getSourceEvent().isSetPayoutEvent()) {
            eventId = value.getSourceEvent().getPayoutEvent().getId();
        } else if (value.getSourceEvent().isSetProcessingEvent()){
            eventId = value.getSourceEvent().getProcessingEvent().getId();
        }
        if (eventId == -1) {
            log.warn("Unknown event. It should be PayoutEvent or ProcessingEvent");
            return;
        }
        lastEventDao.update(eventId);
    }

    @Override
    public Filter getFilter() {
        throw new UnsupportedOperationException("Filter shouldn't use for AnyEventHandler");
    }
}
