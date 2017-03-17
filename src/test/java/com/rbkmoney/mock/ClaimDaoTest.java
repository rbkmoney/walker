package com.rbkmoney.mock;

import com.rbkmoney.WalkerApplicationTests;
import com.rbkmoney.damsel.payment_processing.PartyModification;
import com.rbkmoney.damsel.walker.ClaimSearchRequest;
import com.rbkmoney.damsel.walker.UserInfo;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

    private String TEST_USER = "test_user";

    @Before
    public void before() {
        claimDao.getJdbcTemplate().execute(
                "TRUNCATE TABLE walk.claim CONTINUE IDENTITY RESTRICT;"
        );
    }


    @Test
    public void testInsertAndGet() {
        ClaimRecord claimRecord = new ClaimRecord();
        claimRecord.setId(1l);
        claimRecord.setEventid(10l);
        claimRecord.setAssigned(TEST_USER);
        claimRecord.setChanges("{\"contract_modification\": {\"id\": \"123\", \"modification\": {\"creation\": {\"contractor\": {\"entity\": {\"russian_legal_entity\": {\"inn\": \"АЙНАНЕНАН\", \"post_address\": \"Напишимне напиши\", \"actual_address\": \"Улица пушкина, Дом колотушкина\", \"registered_name\": \"Офшор забугор инкорпарейтед\", \"registered_number\": \"Какая регистрация?\", \"representative_document\": \"Усы лапы и хвост\", \"representative_position\": \"Миссионерская\", \"representative_full_name\": \"Александра Грей\"}}, \"bank_account\": {\"account\": \"Аккаунт\", \"bank_bik\": \"12313\", \"bank_name\": \"Degu Bank Inc\", \"bank_post_account\": \"123123123 post\"}}, \"payout_tool_params\": {\"currency\": {\"symbolic_code\": \"RUB\"}, \"tool_info\": {\"bank_account\": {\"account\": \"Аккаунт\", \"bank_bik\": \"12313\", \"bank_name\": \"Degu Bank Inc\", \"bank_post_account\": \"123123123 post\"}}}}}}}");

        claimDao.create(claimRecord);
        ClaimRecord claimRecord1 = claimDao.get(1);

        assertEquals(claimRecord.getId(), claimRecord1.getId());
        assertEquals(claimRecord.getEventid(), claimRecord1.getEventid());
        assertEquals(String.valueOf(claimRecord.getChanges()), String.valueOf(claimRecord1.getChanges()));


        ClaimSearchRequest claimSearchRequest = new ClaimSearchRequest(new UserInfo());
        claimSearchRequest.setAssigned(TEST_USER);
        claimSearchRequest.setClaimID(Collections.singleton(1L));
        List<ClaimRecord> search = claimDao.search(claimSearchRequest);
        assertEquals(1, search.size());
    }

    @Test
    public void testSearch() {

    }
}
