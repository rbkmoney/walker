package com.rbkmoney;

import com.rbkmoney.walker.WalkerApplication;
import com.rbkmoney.walker.dao.JiraDao;
import com.rbkmoney.walker.handler.poller.JiraPoller;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = WalkerApplication.class)
@Ignore
public class JiraDaoTest {

    @Autowired
    JiraDao jiraDao;

    @Test
    public void getIssue() throws JiraException {
        Issue issueByKey = jiraDao.getIssueByKey("WAL-14");
    }

    @Test
    public void jiraCreateIssue() throws JiraException {
        jiraDao.createIssue(1000, "claim", "partyo", "Head", "Description");
    }

    @Test
    public void getFinishedIssues() throws JiraException {
        jiraDao.getFinishedIssues();
    }

    @Test
    public void getLastEventId() throws JiraException {
        long lastEventId = jiraDao.getLastEventId();
        System.out.println(lastEventId);
    }



}
