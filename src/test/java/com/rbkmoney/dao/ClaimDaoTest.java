package com.rbkmoney.dao;

import com.bazaarvoice.jolt.Diffy;
import com.bazaarvoice.jolt.JsonUtils;

import com.rbkmoney.AbstractIntegrationTest;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.damsel.walker.ClaimSearchRequest;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import com.rbkmoney.walker.service.ActionService;
import com.rbkmoney.walker.utils.ThriftConvertor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.rbkmoney.utils.ActionDiffTest.buildWalkerComplexModification;
import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @since 17.03.17
 **/
public class ClaimDaoTest extends AbstractIntegrationTest {

    @Autowired
    ClaimDao claimDao;

    @Autowired
    ActionService actionService;

    private String TEST_USER_ID = "test_user_id";
    private long CLAIM_ID = 1;

    String PARTY_ID = "test-party-id";

    @Before
    public void before() {
        claimDao.getJdbcTemplate().execute(
                "TRUNCATE TABLE walk.claim CONTINUE IDENTITY RESTRICT;"
        );
    }

    @Test
    public void testInsertAndGet() throws IOException {
        ClaimRecord claimRecord1 = buildTestClaim(PARTY_ID, CLAIM_ID);
        claimDao.create(claimRecord1);
        ClaimRecord claimRecord2 = claimDao.get(PARTY_ID, CLAIM_ID);

        assertEquals(claimRecord1.getId(), claimRecord2.getId());
        assertEquals(claimRecord1.getEventId(), claimRecord2.getEventId());
        assertEquals(claimRecord1.getPartyId(), claimRecord2.getPartyId());

        Object or = JsonUtils.jsonToObject(String.valueOf(claimRecord1.getChanges()));
        Object or1 = JsonUtils.jsonToObject(String.valueOf(claimRecord2.getChanges()));
        Diffy.Result diff = new Diffy().diff(or, or1);
        assertTrue(diff.isEmpty());
    }

    @Test
    public void testUpdateStatus() throws IOException {
        long claimId = CLAIM_ID + 1;
        ClaimRecord claimRecord1 = buildTestClaim(PARTY_ID, claimId);
        claimDao.create(claimRecord1);

        ClaimStatus claimStatus = new ClaimStatus();
        claimStatus.setAccepted(new ClaimAccepted());
        claimDao.updateStatus(PARTY_ID, claimId, claimStatus);
        ClaimRecord claimRecord = claimDao.get(PARTY_ID, claimId);
        assertEquals("accepted", claimRecord.getStatus());
    }


    @Test
    public void testSearch() throws IOException {
        claimDao.create(buildTestClaim(PARTY_ID, CLAIM_ID + 2));
        claimDao.create(buildTestClaim(PARTY_ID, CLAIM_ID + 3));

        ClaimSearchRequest claimSearchRequest = new ClaimSearchRequest();
        claimSearchRequest.setAssignedUserId(TEST_USER_ID);
        claimSearchRequest.setPartyId(PARTY_ID);
        claimSearchRequest.setClaimId(Collections.singleton(CLAIM_ID + 2));
        claimSearchRequest.setClaimStatus("pending");
        List<ClaimRecord> search = claimDao.search(claimSearchRequest);
        assertEquals(1, search.size());
    }

    public String buildModification() throws IOException {
        PartyModificationUnit partyModificationUnit = new PartyModificationUnit();
        List<PartyModification> partyModificationList = Arrays.asList(buildWalkerComplexModification());
        partyModificationUnit.setModifications(partyModificationList);
        return ThriftConvertor.convertToJson(partyModificationUnit);
    }

    private ClaimRecord buildTestClaim(String partyId, long claimId) throws IOException {
        ClaimRecord claimRecord = new ClaimRecord();
        claimRecord.setStatus(ClaimStatus.pending(new ClaimPending()).toString());
        claimRecord.setId(claimId);
        claimRecord.setEventId(10l);
        claimRecord.setAssignedUserId(TEST_USER_ID);
        claimRecord.setRevision(10L);
        claimRecord.setPartyId(partyId);
        claimRecord.setChanges(buildModification());
        return claimRecord;
    }
}
