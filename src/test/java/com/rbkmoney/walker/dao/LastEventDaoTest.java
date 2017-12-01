package com.rbkmoney.walker.dao;

import com.rbkmoney.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LastEventDaoTest extends AbstractIntegrationTest {

    @Autowired
    LastEventDao lastEventDao;

    @Before
    public void setUp() {
        Assert.assertNull(lastEventDao.getLastEventId());
    }

    @Test
    public void update() {
        lastEventDao.update(1);
        Assert.assertEquals(lastEventDao.getLastEventId(), new Long(1));
    }
}