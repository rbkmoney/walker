package com.rbkmoney.dao;

import com.bazaarvoice.jolt.Diffy;
import com.bazaarvoice.jolt.JsonUtils;
import com.rbkmoney.AbstractIntegrationTest;
import com.rbkmoney.damsel.payment_processing.ClaimAccepted;
import com.rbkmoney.damsel.payment_processing.ClaimPending;
import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.payment_processing.PartyModification;
import com.rbkmoney.damsel.walker.ClaimSearchRequest;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import com.rbkmoney.walker.utils.ThriftConvertor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.rbkmoney.utils.ActionDiffTest.buildLegalAgreement;
import static com.rbkmoney.utils.ActionDiffTest.buildWalkerComplexModification;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ClaimDaoTest extends AbstractIntegrationTest {

    private static final String TEST_USER_ID = "test_user_id";
    private static final String PARTY_ID = "test-party-id";
    private static final long CLAIM_ID = 1;
    @Autowired
    private ClaimDao claimDao;

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
    public void testInsertAndUpdate() throws IOException {
        ClaimRecord claimRecord1 = buildTestClaim(PARTY_ID, CLAIM_ID);
        claimDao.create(claimRecord1);

        String modification = buildAdjustmentModification();
        claimDao.update(PARTY_ID, CLAIM_ID, 22L, 10L, modification);
        ClaimRecord claimRecord3 = claimDao.get(PARTY_ID, CLAIM_ID);

        assertEquals((Long) 10L, claimRecord3.getRevision());
        assertEquals(Long.valueOf(22L), claimRecord3.getEventId());
        assertEquals(Long.valueOf(10L), claimRecord3.getRevision());
        assertEquals(modification, String.valueOf(claimRecord3.getChanges()).replace(" ", ""));
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
        claimDao.create(buildTestAcceptedClaim(PARTY_ID, CLAIM_ID + 3));
        claimDao.create(buildTestClaim(PARTY_ID + "2", CLAIM_ID + 2));
        claimDao.create(buildTestAcceptedClaim(PARTY_ID + "2", CLAIM_ID + 3));

        ClaimSearchRequest claimSearchRequest = new ClaimSearchRequest();
        claimSearchRequest.setAssignedUserId(TEST_USER_ID);
        claimSearchRequest.setPartyId(PARTY_ID);
        claimSearchRequest.setClaimId(Collections.singleton(CLAIM_ID + 3));
        claimSearchRequest.setClaimStatus("accepted");

        List<ClaimRecord> search = claimDao.search(claimSearchRequest);
        assertEquals(1, search.size());

        claimSearchRequest.setPartyId(PARTY_ID + "2");
        List<ClaimRecord> search2 = claimDao.search(claimSearchRequest);
        assertEquals(1, search2.size());
    }

    public String buildModification() throws IOException {
        PartyModificationUnit partyModificationUnit = new PartyModificationUnit();
        List<PartyModification> partyModificationList = Arrays.asList(buildWalkerComplexModification());
        partyModificationUnit.setModifications(partyModificationList);
        return ThriftConvertor.convertToJson(partyModificationUnit);
    }

    public String buildAdjustmentModification() throws IOException {
        PartyModificationUnit partyModificationUnit = new PartyModificationUnit();
        List<PartyModification> partyModificationList = Collections.singletonList(buildLegalAgreement());
        partyModificationUnit.setModifications(partyModificationList);
        return ThriftConvertor.convertToJson(partyModificationUnit);
    }

    private ClaimRecord buildTestClaim(String partyId, long claimId) throws IOException {
        ClaimRecord claimRecord = new ClaimRecord();
        claimRecord.setStatus(ClaimDao.getStatusName(ClaimStatus.pending(new ClaimPending())));
        claimRecord.setId(claimId);
        claimRecord.setEventId(10L);
        claimRecord.setAssignedUserId(TEST_USER_ID);
        claimRecord.setRevision(10L);
        claimRecord.setPartyId(partyId);
        claimRecord.setChanges(buildModification());
        return claimRecord;
    }

    private ClaimRecord buildTestAcceptedClaim(String partyId, long claimId) throws IOException {
        ClaimRecord claimRecord = new ClaimRecord();
        claimRecord.setStatus(ClaimDao.getStatusName(ClaimStatus.accepted(new ClaimAccepted())));
        claimRecord.setId(claimId);
        claimRecord.setEventId(10L);
        claimRecord.setAssignedUserId(TEST_USER_ID);
        claimRecord.setRevision(10L);
        claimRecord.setPartyId(partyId);
        claimRecord.setChanges(buildModification());
        return claimRecord;
    }

    @Test
    public void testPrint() throws IOException {
        String json = buildModification();
        System.out.println(json);
    }
}
