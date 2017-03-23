package com.rbkmoney.dao;

import com.bazaarvoice.jolt.Diffy;
import com.rbkmoney.WalkerApplicationTests;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.damsel.walker.ClaimSearchRequest;
import com.rbkmoney.damsel.walker.UserInfo;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import com.rbkmoney.walker.service.ActionService;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @since 17.03.17
 **/
public class ClaimDaoTest extends WalkerApplicationTests {

    @Autowired
    ClaimDao claimDao;

    @Autowired
    ActionService actionService;

    private String TEST_USER = "test_user";

    @Before
    public void before() {
        claimDao.getJdbcTemplate().execute(
                "TRUNCATE TABLE walk.claim CONTINUE IDENTITY RESTRICT;"
        );
    }

    @Test
    public void testInsertAndGet() {
        ClaimRecord claimRecord = buildTestClaim();
        claimDao.create(claimRecord);
        ClaimRecord claimRecord1 = claimDao.get(1);

        assertEquals(claimRecord.getId(), claimRecord1.getId());
        assertEquals(claimRecord.getEventId(), claimRecord1.getEventId());
        assertEquals(String.valueOf(claimRecord.getChanges()), String.valueOf(claimRecord1.getChanges()));

        ClaimSearchRequest claimSearchRequest = new ClaimSearchRequest();
        claimSearchRequest.setAssigned(TEST_USER);
        claimSearchRequest.setClaimID(Collections.singleton(1L));
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
        actionService.claimStatusChanged(1L, claimStatus,"testUser");
    }

    @Test
    public void testSearch() {
        claimDao.create(buildTestClaim());

        ClaimRecord claimRecord1 = claimDao.get(1);
        claimRecord1.setEventId(123L);
        claimRecord1.setChanges("{\"contract_modification\": {\"id\": \"222\", \"modification\": {\"creation\": {\"contractor\": {\"entity\": {\"russian_legal_entity\": {\"inn\": \"АЙНАНЕНАН\", \"post_address\": \"Напишимне напиши\", \"actual_address\": \"Улица пушкина, Дом колотушкина\", \"registered_name\": \"Офшор забугор инкорпарейтед\", \"registered_number\": \"Какая регистрация?\", \"representative_document\": \"Усы лапы и хвост\", \"representative_position\": \"Миссионерская\", \"representative_full_name\": \"Александра Грей\"}}, \"bank_account\": {\"account\": \"Аккаунт\", \"bank_bik\": \"12313\", \"bank_name\": \"Degu Bank Inc\", \"bank_post_account\": \"123123123 post\"}}, \"payout_tool_params\": {\"currency\": {\"symbolic_code\": \"RUB\"}, \"tool_info\": {\"bank_account\": {\"account\": \"Аккаунт\", \"bank_bik\": \"12313\", \"bank_name\": \"Degu Bank Inc\", \"bank_post_account\": \"AFTER_UPDATE\"}}}}}}}");
        claimDao.update(claimRecord1);

        ClaimRecord claimRecord2 = claimDao.get(claimRecord1.getId());


        assertEquals(Long.valueOf(123L), claimRecord2.getEventId());
        assertTrue(StringUtils.contains(String.valueOf(claimRecord2.getChanges()), "AFTER_UPDATE"));
    }

    private ClaimRecord buildTestClaim() {
        ClaimRecord claimRecord = new ClaimRecord();
        claimRecord.setStatus(ClaimStatus.pending(new ClaimPending()).toString());
        claimRecord.setId(1l);
        claimRecord.setEventId(10l);
        claimRecord.setAssigned(TEST_USER);
        claimRecord.setChanges("{\"contract_modification\": {\"id\": \"123\", \"modification\": {\"creation\": {\"contractor\": {\"entity\": {\"russian_legal_entity\": {\"inn\": \"АЙНАНЕНАН\", \"post_address\": \"Напишимне напиши\", \"actual_address\": \"Улица пушкина, Дом колотушкина\", \"registered_name\": \"Офшор забугор инкорпарейтед\", \"registered_number\": \"Какая регистрация?\", \"representative_document\": \"Усы лапы и хвост\", \"representative_position\": \"Миссионерская\", \"representative_full_name\": \"Александра Грей\"}}, \"bank_account\": {\"account\": \"Аккаунт\", \"bank_bik\": \"12313\", \"bank_name\": \"Degu Bank Inc\", \"bank_post_account\": \"123123123 post\"}}, \"payout_tool_params\": {\"currency\": {\"symbolic_code\": \"RUB\"}, \"tool_info\": {\"bank_account\": {\"account\": \"Аккаунт\", \"bank_bik\": \"12313\", \"bank_name\": \"Degu Bank Inc\", \"bank_post_account\": \"123123123 post\"}}}}}}}");
        return claimRecord;
    }
}
