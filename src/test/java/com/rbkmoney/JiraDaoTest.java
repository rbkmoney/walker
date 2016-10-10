package com.rbkmoney;

import com.rbkmoney.config.TestJiraConfig;
import com.rbkmoney.walker.dao.JiraDao;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestJiraConfig.class)
@Ignore
public class JiraDaoTest {

    @Autowired
    JiraDao jiraDao;

    @Test
    public void getIssue() throws JiraException {
        Issue issueByKey = jiraDao.getIssueByKey("CLAIM-3");
        System.out.println(issueByKey.getId());
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
