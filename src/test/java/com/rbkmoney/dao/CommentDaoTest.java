package com.rbkmoney.dao;

import com.rbkmoney.AbstractIntegrationTest;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.dao.CommentDao;
import com.rbkmoney.walker.domain.generated.tables.records.ActionRecord;
import com.rbkmoney.walker.domain.generated.tables.records.CommentRecord;
import com.rbkmoney.walker.service.ActionService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @since 28.03.17
 **/
public class CommentDaoTest extends AbstractIntegrationTest {

    @Autowired
    CommentDao commentDao;


    @Before
    public void before() {
        commentDao.getJdbcTemplate().execute(
                "TRUNCATE TABLE walk.claim CONTINUE IDENTITY RESTRICT;"
        );
    }

    @Test
    public void test() {
        CommentRecord commentRecord = new CommentRecord();
        commentRecord.setUserId("2");
        commentRecord.setClaimId(1L);
        commentRecord.setText("Буууу");
        commentRecord.setPartyId("test-party-id");

        commentDao.add(commentRecord);
        List<CommentRecord> comments = commentDao.getComments(1L);
        assertEquals(comments.get(0).getText(), "Буууу");
        assertEquals(comments.get(0).getClaimId(), (Long) 1L);
    }


}
