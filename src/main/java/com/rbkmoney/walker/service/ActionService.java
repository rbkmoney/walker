package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.payment_processing.PartyModification;
import com.rbkmoney.damsel.walker.ActionModification;
import com.rbkmoney.damsel.walker.ClaimChengsest;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.walker.dao.ActionDao;
import com.rbkmoney.walker.domain.generated.tables.records.ActionRecord;
import com.rbkmoney.walker.handler.PartyEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



/**
 * @since 22.03.17
 **/
@Service
public class ActionService {

    @Autowired
    private ActionDao actionDao;

    public void claimCreated(List<PartyModification> changeset, String userId) throws IOException {
        //TODO переписать все нахуй
        PartyModificationUnit partyModificationUnit = new PartyEventHandler().convertToPartyModificationUnit(changeset);
        ClaimChengsest claimChengsest = new ClaimChengsest();
        claimChengsest.setAfter(partyModificationUnit);

        ActionModification actionModification = new ActionModification();
        actionModification.setClaimChengsest(claimChengsest);


        String s = actionDao.toStringJson(actionModification);
        ActionRecord actionRecord = new ActionRecord();
        actionRecord.setUserId(userId);
        actionRecord.setModification(s);
        actionDao.add(actionRecord);
    }
}
