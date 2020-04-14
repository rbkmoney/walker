package com.rbkmoney.walker.handler.party.claim;

import com.rbkmoney.damsel.payment_processing.Claim;
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

import static com.rbkmoney.walker.dao.ClaimDao.getStatusName;
import static com.rbkmoney.walker.utils.ThriftConvertor.convertToJson;
import static com.rbkmoney.walker.utils.ThriftConvertor.convertToPartyModificationUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimCreatedHandler implements PartyChangeEventHandler {

    private final ClaimDao claimDao;

    private final ActionService actionService;

    @Override
    public void handleEvent(PartyChange partyChange, long eventId, String partyId) throws IOException {
        Claim claim = partyChange.getClaimCreated();
        log.info("Got claim created PartyId: {}, ClaimId: {}", partyId, claim.getId());
        ClaimRecord claimRecord = new ClaimRecord();
        claimRecord.setId(claim.getId());
        claimRecord.setEventId(eventId);
        claimRecord.setRevision((long) claim.getRevision());
        claimRecord.setPartyId(partyId);
        claimRecord.setStatus(getStatusName(claim.getStatus()));
        claimRecord.setDescription("Заявка " + claim.getId() + " от участника с PartyId " + partyId);

        PartyModificationUnit partyModificationUnit = convertToPartyModificationUnit(claim.getChangeset());
        claimRecord.setChanges(convertToJson(partyModificationUnit));
        claimDao.create(claimRecord);
        actionService.claimCreated(partyId, claim.getId(), claim.getChangeset(), "event");
    }

}
