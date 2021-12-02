package com.rbkmoney.service;

import com.rbkmoney.AbstractIntegrationTest;
import com.rbkmoney.damsel.payment_processing.ClaimDenied;
import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.walker.Action;
import com.rbkmoney.damsel.walker.ActionType;
import com.rbkmoney.damsel.walker.Comment;
import com.rbkmoney.damsel.walker.UserInformation;
import com.rbkmoney.damsel.walker.WalkerSrv;
import com.rbkmoney.walker.dao.ActionDao;
import com.rbkmoney.walker.dao.CommentDao;
import com.rbkmoney.walker.service.ActionService;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WalkerServiceTest extends AbstractIntegrationTest {

    private static final String TEST_USER_ID = "test_user_id";
    private static final String PARTY_ID = "test-party-id";
    private static final long CLAIM_ID = 1;
    @Autowired
    private ActionService actionService;
    @Autowired
    private ActionDao actionDao;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private WalkerSrv.Iface walkerService;

    @BeforeEach
    public void before() {
        actionDao.getJdbcTemplate()
                .execute("TRUNCATE TABLE walk.ACTION CONTINUE IDENTITY RESTRICT;");
        commentDao.getJdbcTemplate().execute(
                "TRUNCATE TABLE walk.comment CONTINUE IDENTITY RESTRICT;");
    }

    @Test
    public void addActionTest() throws IOException, TException {
        ClaimStatus claimStatus = new ClaimStatus();
        ClaimDenied claimDenied = new ClaimDenied();
        claimDenied.setReason("because");
        claimStatus.setDenied(claimDenied);
        actionService.claimStatusChanged(PARTY_ID, 1L, claimStatus, TEST_USER_ID, "2020-02-02");
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
