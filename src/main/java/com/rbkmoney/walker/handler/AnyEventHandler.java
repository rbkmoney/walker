package com.rbkmoney.walker.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.walker.dao.LastEventDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AnyEventHandler implements Handler<StockEvent> {

    private String path = "source_event.processing_event";

    private Filter filter;

    public AnyEventHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path, new IsNullCondition().not()));
    }

    @Autowired
    private LastEventDao lastEventDao;

    @Override
    public void handle(StockEvent value) {
        long eventId = value.getSourceEvent().getProcessingEvent().getId();
        lastEventDao.update(eventId);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
