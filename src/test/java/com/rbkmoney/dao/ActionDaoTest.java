package com.rbkmoney.dao;

import com.rbkmoney.AbstractIntegrationTest;
import com.rbkmoney.damsel.payment_processing.PartyModification;
import com.rbkmoney.damsel.walker.ActionType;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.walker.dao.ActionDao;
import com.rbkmoney.walker.domain.generated.tables.records.ActionRecord;
import com.rbkmoney.walker.utils.ThriftConvertor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.rbkmoney.utils.ActionDiffTest.buildWalkerComplexModification;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionDaoTest extends AbstractIntegrationTest {

    private static final String PARTY_ID = "test-party-id";
    private static final String TEST_USER_ID = "test_user_id";
    private static final long CLAIM_ID = 1L;
    @Autowired
    private ActionDao actionDao;

    @BeforeEach
    public void before() {
        actionDao.getJdbcTemplate()
                .execute("TRUNCATE TABLE walk.ACTION CONTINUE IDENTITY RESTRICT;");
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
        actionRecord.setPartyId(PARTY_ID);
        actionRecord.setEventCreatedAt(LocalDateTime.now().toString());
        actionDao.add(actionRecord);
        actionDao.add(actionRecord);

        List<ActionRecord> actions = actionDao.getActions(PARTY_ID, CLAIM_ID);

        assertEquals(1, actions.size());
        assertEquals(PARTY_ID, actions.get(0).getPartyId());
        assertEquals(Long.valueOf(CLAIM_ID), actions.get(0).getClaimId());
        assertEquals(actions.get(0).getType(), ActionType.claim_changed.toString());
    }

    public String buildModification() throws IOException {
        PartyModificationUnit partyModificationUnit = new PartyModificationUnit();
        List<PartyModification> partyModificationList = Arrays.asList(buildWalkerComplexModification());
        partyModificationUnit.setModifications(partyModificationList);
        return ThriftConvertor.convertToJson(partyModificationUnit);
    }

}
