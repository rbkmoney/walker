package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.payment_processing.PartyModification;
import com.rbkmoney.damsel.walker.ActionType;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.walker.dao.ActionDao;
import com.rbkmoney.walker.domain.generated.tables.records.ActionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.rbkmoney.walker.dao.ClaimDao.getStatusName;
import static com.rbkmoney.walker.domain.generated.Tables.CLAIM;
import static com.rbkmoney.walker.service.ThriftObjectsConvertor.convertToJson;


/**
 * @since 22.03.17 👩‍🎤
 **/
@Service
public class ActionService {

    @Autowired
    private ActionDao actionDao;

    public void claimCreated(Long claimId, List<PartyModification> changeset, String userId) throws IOException {
        PartyModificationUnit partyModificationUnit = ThriftObjectsConvertor.convertToPartyModificationUnit(changeset);
        String modificationString = convertToJson(partyModificationUnit);

        ActionRecord actionRecord = new ActionRecord();
        actionRecord.setUserId(userId);
        actionRecord.setType(ActionType.claim_changed.toString());
        actionRecord.setClaimId(claimId);
        actionRecord.setAfter(modificationString);
        actionDao.add(actionRecord);
    }

    public void claimStatusChanged(Long claimId, ClaimStatus claimStatus, String userId) throws IOException {
        String json = convertToJson(claimStatus);
        ActionRecord actionRecord = new ActionRecord();
        actionRecord.setType(ActionType.status_changed.toString());
        actionRecord.setAfter(json);
        actionRecord.setClaimId(claimId);
        actionRecord.setUserId(userId);
        actionDao.add(actionRecord);
    }
}
