package com.rbkmoney.walker.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Claim;
import com.rbkmoney.damsel.payment_processing.ClaimRevoked;
import com.rbkmoney.damsel.payment_processing.ClaimStatusChanged;
import com.rbkmoney.damsel.payment_processing.PartyEvent;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import com.rbkmoney.walker.dao.JiraDao;
import com.rbkmoney.walker.handler.Handler;
import net.rcarz.jiraclient.JiraException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClaimStatusChangedEventHandler implements Handler<StockEvent> {
    Logger log = LoggerFactory.getLogger(this.getClass());
    // tricky - filter pass must be at least 1 element longer then you want
    private String path = "source_event.processing_event.payload.party_event.claim_status_changed.id";

    private Filter filter;

    public ClaimStatusChangedEventHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }

    @Autowired
    JiraDao jiraDao;

    @Override
    //todo refactor
    public void handle(StockEvent value) {
        long eventId = value.getSourceEvent().getProcessingEvent().getId();
        log.info("Got StatusChanged event {}", eventId);

        ClaimStatusChanged claimStatusChanged = value.getSourceEvent().getProcessingEvent().getPayload().getPartyEvent().getClaimStatusChanged();
        if (claimStatusChanged.getStatus().isSetRevoked()) {
            try {
                jiraDao.closeRevokedIssue(
                        claimStatusChanged.getId(),
                        claimStatusChanged.getStatus().getRevoked().getReason());
            } catch (JiraException e) {
                e.printStackTrace();
            }
        } else if (claimStatusChanged.getStatus().isSetAccepted() || claimStatusChanged.getStatus().isSetDenied()) {
            try {
                jiraDao.closeIssue(claimStatusChanged.getId());
            } catch (JiraException e) {
               log.error("Cant close Accepted or Denied claim with id {}", claimStatusChanged.getId());
            }
        } else {
            log.error("Unsupported ClaimStatus changing for eventId : {}", eventId);
        }

    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
