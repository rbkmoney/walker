package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.payment_processing.PartyModification;
import com.rbkmoney.damsel.walker.ActionType;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.walker.dao.ActionDao;
import com.rbkmoney.walker.domain.generated.tables.records.ActionRecord;
import com.rbkmoney.walker.utils.ThriftConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.rbkmoney.walker.utils.ThriftConvertor.convertToJson;

@Service
@RequiredArgsConstructor
public class ActionService {

    private final ActionDao actionDao;

    public void claimCreated(String partyId,
                             Long claimId,
                             List<PartyModification> changeset,
                             String userId,
                             String createdAt) throws IOException {
        PartyModificationUnit partyModificationUnit = ThriftConvertor.convertToPartyModificationUnit(changeset);
        String modificationString = convertToJson(partyModificationUnit);

        ActionRecord actionRecord = new ActionRecord();
        actionRecord.setUserId(userId);
        actionRecord.setType(ActionType.claim_changed.toString());
        actionRecord.setClaimId(claimId);
        actionRecord.setAfter(modificationString);
        actionRecord.setPartyId(partyId);
        actionRecord.setEventCreatedAt(createdAt);
        actionDao.add(actionRecord);
    }

    public void claimUpdated(String partyId,
                             Long claimId,
                             List<PartyModification> changeset,
                             String userId,
                             String updatedAt) throws IOException {
        PartyModificationUnit partyModificationUnit = ThriftConvertor.convertToPartyModificationUnit(changeset);
        String modificationString = convertToJson(partyModificationUnit);
        ActionRecord actionRecord = new ActionRecord();
        actionRecord.setPartyId(partyId);
        actionRecord.setUserId(userId);
        actionRecord.setType(ActionType.claim_changed.toString());
        actionRecord.setClaimId(claimId);
        actionRecord.setAfter(modificationString);
        actionRecord.setEventCreatedAt(updatedAt);
        actionDao.add(actionRecord);
    }

    public void claimStatusChanged(String partyId,
                                   Long claimId,
                                   ClaimStatus claimStatus,
                                   String userId,
                                   String changedAt) throws IOException {
        String json = convertToJson(claimStatus);
        ActionRecord actionRecord = new ActionRecord();
        actionRecord.setPartyId(partyId);
        actionRecord.setType(ActionType.status_changed.toString());
        actionRecord.setAfter(json);
        actionRecord.setClaimId(claimId);
        actionRecord.setUserId(userId);
        actionRecord.setEventCreatedAt(changedAt);
        actionDao.add(actionRecord);
    }

}
