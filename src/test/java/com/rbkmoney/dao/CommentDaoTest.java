package com.rbkmoney.dao;

import com.rbkmoney.AbstractIntegrationTest;
import com.rbkmoney.walker.dao.CommentDao;
import com.rbkmoney.walker.domain.generated.tables.records.CommentRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentDaoTest extends AbstractIntegrationTest {

    private static final String PARTY_ID = "test-party-id";
    @Autowired
    private CommentDao commentDao;

    @BeforeEach
    public void before() {
        commentDao.getJdbcTemplate().execute(
                "TRUNCATE TABLE walk.comment CONTINUE IDENTITY RESTRICT;"
        );
    }

    @Test
    public void test() {
        CommentRecord commentRecord = new CommentRecord();
        commentRecord.setUserId("2");
        commentRecord.setClaimId(1L);
        commentRecord.setText("Буууу");
        commentRecord.setPartyId(PARTY_ID);

        commentDao.add(commentRecord);

        commentRecord.setClaimId(2L);
        commentDao.add(commentRecord);

        List<CommentRecord> comments = commentDao.getComments(PARTY_ID, 1L);

        assertEquals(1, comments.size());
        assertEquals("Буууу", comments.get(0).getText());
        assertEquals((Long) 1L, comments.get(0).getClaimId());
    }

}
