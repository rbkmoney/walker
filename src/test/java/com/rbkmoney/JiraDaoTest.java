package com.rbkmoney;

import com.rbkmoney.config.TestJiraConfig;
import com.rbkmoney.walker.dao.JiraDao;
import net.rcarz.jiraclient.*;
import net.sf.json.JSON;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestJiraConfig.class)
@Ignore
public class JiraDaoTest {

    @Autowired
    JiraDao jiraDao;

    @Test
    public void getTypes() throws JiraException {
        List<IssueType> types = jiraDao.getTypes();
        System.out.println(
                types.get(0).toString()
        );
    }

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

    @Test
    public void deleteIssue() throws URISyntaxException, IOException, RestException {
        //CLAIM-8
        JSON json = jiraDao.getJiraClient().getRestClient().delete("/rest/api/2/issue/CLAIM-9");
        System.out.printf(String.valueOf(json));

    }

}
