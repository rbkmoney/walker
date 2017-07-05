package com.rbkmoney.service;

import com.rbkmoney.AbstractIntegrationTest;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.damsel.walker.*;
import com.rbkmoney.walker.dao.ActionDao;
import com.rbkmoney.walker.dao.CommentDao;
import com.rbkmoney.walker.service.ActionService;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @since 03.07.17
 **/
public class WalkerServiceTest extends AbstractIntegrationTest {

    @Autowired
    ActionService actionService;

    @Autowired
    ActionDao actionDao;

    @Autowired
    CommentDao commentDao;


    @Autowired
    WalkerSrv.Iface walkerService;


    private String TEST_USER_ID = "test_user_id";
    private long CLAIM_ID = 1;
    private String PARTY_ID = "test-party-id";

    @Before
    public void before() {
        actionDao.getJdbcTemplate()
                .execute("TRUNCATE TABLE walk.ACTION CONTINUE IDENTITY RESTRICT;");
        commentDao.getJdbcTemplate().execute(
                "TRUNCATE TABLE walk.comment CONTINUE IDENTITY RESTRICT;");
    }

    @Test
    public void addActionTest() throws IOException, TException {
        ClaimStatus claimStatus = new ClaimStatus();
        claimStatus.setDenied(new ClaimDenied("because"));
        actionService.claimStatusChanged(PARTY_ID, 1L, claimStatus, TEST_USER_ID);
        List<Action> actions = walkerService.getActions(PARTY_ID, 1L);
        assertEquals(1, actions.size());
        assertEquals(ActionType.status_changed, actions.get(0).getType());
    }

    @Test
    public void testComment() throws TException {
        walkerService.addComment(PARTY_ID, CLAIM_ID, buildTestUser(), "Test comment");
        walkerService.addComment(PARTY_ID, CLAIM_ID + 1, buildTestUser(), "Test comment 2");
        List<Comment> comments = walkerService.getComments(PARTY_ID, CLAIM_ID);

        assertEquals(1, comments.size());
        assertEquals("Test comment", comments.get(0).getText());
    }

    private UserInformation buildTestUser() {
        UserInformation userInformation = new UserInformation();
        userInformation.setUserID("testId");
        return userInformation;
    }
}
