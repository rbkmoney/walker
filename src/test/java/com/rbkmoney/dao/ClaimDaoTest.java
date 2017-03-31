package com.rbkmoney.dao;

import com.bazaarvoice.jolt.Diffy;
import com.bazaarvoice.jolt.JsonUtils;

import com.rbkmoney.AbstractIntegrationTest;
import com.rbkmoney.damsel.payment_processing.ClaimAccepted;
import com.rbkmoney.damsel.payment_processing.ClaimDenied;
import com.rbkmoney.damsel.payment_processing.ClaimPending;
import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.walker.ClaimSearchRequest;
import com.rbkmoney.damsel.walker.PartyModification;
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

import static com.rbkmoney.ActionDiffTest.buildWalkerComplexModification;
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

    @Before
    public void before() {
        claimDao.getJdbcTemplate().execute(
                "TRUNCATE TABLE walk.claim CONTINUE IDENTITY RESTRICT;"
        );
    }

    @Test
    public void testInsertAndGet() throws IOException {
        ClaimRecord claimRecord = buildTestClaim();
        claimDao.create(claimRecord);
        ClaimRecord claimRecord1 = claimDao.get(1);

        assertEquals(claimRecord.getId(), claimRecord1.getId());
        assertEquals(claimRecord.getEventId(), claimRecord1.getEventId());

        Object or = JsonUtils.jsonToObject(String.valueOf(claimRecord.getChanges()));
        Object or1 = JsonUtils.jsonToObject(String.valueOf(claimRecord1.getChanges()));
        Diffy.Result diff = new Diffy().diff(or, or1);
        assertTrue(diff.isEmpty());

        ClaimSearchRequest claimSearchRequest = new ClaimSearchRequest();
        claimSearchRequest.setAssignedUserId(TEST_USER_ID);
        claimSearchRequest.setClaimId(Collections.singleton(1L));
        List<ClaimRecord> search = claimDao.search(claimSearchRequest);
        assertEquals(1, search.size());
    }

    @Test
    public void testUpdateStatus() {
        ClaimStatus claimStatus = new ClaimStatus();
        claimStatus.setAccepted(new ClaimAccepted());
        claimDao.updateStatus(2, claimStatus);
    }

    @Test
    public void testActions() throws IOException {
        ClaimStatus claimStatus = new ClaimStatus();
        claimStatus.setDenied(new ClaimDenied("because"));
        actionService.claimStatusChanged(1L, claimStatus, TEST_USER_ID);
    }

    @Test
    public void testSearch() throws IOException {
        claimDao.create(buildTestClaim());
        ClaimRecord claimRecord1 = claimDao.get(1);
        claimRecord1.setEventId(123L);
        claimRecord1.setChanges(buildModification());
        claimDao.update(claimRecord1);

        ClaimRecord claimRecord2 = claimDao.get(claimRecord1.getId());


        assertEquals(Long.valueOf(123L), claimRecord2.getEventId());
//        assertTrue(StringUtils.contains(String.valueOf(claimRecord2.getChanges()), "AFTER_UPDATE"));
    }

    public String buildModification() throws IOException {
        PartyModificationUnit partyModificationUnit = new PartyModificationUnit();
        List<PartyModification> partyModificationList = Arrays.asList(buildWalkerComplexModification());
        partyModificationUnit.setModifications(partyModificationList);
        return ThriftConvertor.convertToJson(partyModificationUnit);
    }

    private ClaimRecord buildTestClaim() throws IOException {
        ClaimRecord claimRecord = new ClaimRecord();
        claimRecord.setStatus(ClaimStatus.pending(new ClaimPending()).toString());
        claimRecord.setId(1l);
        claimRecord.setEventId(10l);
        claimRecord.setAssignedUserId(TEST_USER_ID);
        claimRecord.setRevision(10L);
        claimRecord.setChanges(buildModification());
        return claimRecord;
    }
}
