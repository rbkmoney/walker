package com.rbkmoney.walker.handler.party.claim;

import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import com.rbkmoney.walker.handler.party.PartyChangeEventHandler;
import com.rbkmoney.walker.service.ActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.rbkmoney.walker.utils.ThriftConvertor.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimUpdatedHandler implements PartyChangeEventHandler {

    private final ClaimDao claimDao;

    private final ActionService actionService;

    @Override
    public void handleEvent(PartyChange partyChange, long eventId, String partyId) throws IOException {
        long claimId = partyChange.getClaimUpdated().getId();
        log.info("Got claim updated PartyId: {}, ClaimId: {}", partyId, claimId);
        ClaimRecord claimRecord = claimDao.get(partyId, claimId);
        PartyModificationUnit partyModificationUnit =
                fromJsonPartyModificationUnit(String.valueOf(claimRecord.getChanges()));

        partyModificationUnit.getModifications().addAll(
                convertToPartyModificationUnit(partyChange.getClaimUpdated().getChangeset()).getModifications());

        Long revision = (long) partyChange.getClaimUpdated().getRevision();

        claimDao.update(partyId, claimId, eventId, revision, convertToJson(partyModificationUnit));
        actionService.claimUpdated(partyId, claimId, partyChange.getClaimUpdated().getChangeset(), "event");
    }

}
