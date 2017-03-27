package com.rbkmoney.dao;

import com.bazaarvoice.jolt.JsonUtils;
import com.bazaarvoice.jolt.utils.JoltUtils;
import com.rbkmoney.WalkerApplicationTests;
import com.rbkmoney.damsel.walker.*;
import com.rbkmoney.damsel.walker.PartyModification;
import com.rbkmoney.geck.serializer.kit.object.ObjectHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import com.rbkmoney.walker.dao.ActionDao;
import com.rbkmoney.walker.domain.generated.tables.records.ActionRecord;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.rbkmoney.ActionDiffTest.buildWalkerComplexModification;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @since 20.03.17
 **/
public class ActionDaoTest extends WalkerApplicationTests {

    @Autowired
    private ActionDao actionDao;

    @Before
    public void before() {
        actionDao.getJdbcTemplate().execute(
                "TRUNCATE TABLE walk.ACTION CONTINUE IDENTITY RESTRICT;"
        );
    }

    @Test
    public void testInsert() throws IOException {
        ActionRecord actionRecord = new ActionRecord();
        actionRecord.setClaimId(1L);
        actionRecord.setUserId("10");
        actionRecord.setUserEmail("test@email.com");
        actionRecord.setUserName("userName");
        actionRecord.setType(ActionType.claim_changed.toString());
        actionRecord.setAfter(buildModification());
        actionDao.add(actionRecord);

        List<ActionRecord> actionRecords = actionDao.getActionsByClaimId(1L);
        assertTrue(actionRecords.size() == 1);
        assertTrue(actionRecords.get(0).getType().equals(ActionType.claim_changed.toString()));
    }

    public String buildModification() throws IOException {
        PartyModificationUnit partyModificationUnit = new PartyModificationUnit();
        List<PartyModification> partyModificationList = Arrays.asList(buildWalkerComplexModification());
        partyModificationUnit.setModifications(partyModificationList);

        ClaimChengsest claimChengsest = new ClaimChengsest();
        claimChengsest.setAfter(partyModificationUnit);

        ActionModification actionModification = new ActionModification();
        actionModification.setClaimChengsest(claimChengsest);

        Object o = JoltUtils.compactJson(new TBaseProcessor().process(actionModification, new ObjectHandler()));
        return JsonUtils.toJsonString(o);
    }


}