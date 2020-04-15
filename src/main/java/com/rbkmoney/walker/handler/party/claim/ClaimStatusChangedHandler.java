package com.rbkmoney.walker.handler.party.claim;

import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.payment_processing.ClaimStatusChanged;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.handler.party.PartyChangeEventHandler;
import com.rbkmoney.walker.service.ActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimStatusChangedHandler implements PartyChangeEventHandler {

    private final ClaimDao claimDao;

    private final ActionService actionService;

    @Override
    public void handleEvent(PartyChange partyChange, long eventId, String partyId) throws IOException {
        ClaimStatusChanged claimStatusChanged = partyChange.getClaimStatusChanged();
        long claimId = claimStatusChanged.getId();
        ClaimStatus status = claimStatusChanged.getStatus();
        log.info("Got claim status changed Status: {}, PartyId: {}, ClaimId: {}", status.toString(), partyId, claimId);
        claimDao.updateStatus(partyId, claimId, status);
        actionService.claimStatusChanged(partyId, claimId, status, "event", claimStatusChanged.getChangedAt());
    }

}
