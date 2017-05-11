package com.rbkmoney;

import com.rbkmoney.config.TestJiraConfig;
import com.rbkmoney.damsel.event_stock.SourceEvent;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.walker.dao.JiraDao;
import com.rbkmoney.walker.handler.PartyEventHandler;
import net.rcarz.jiraclient.*;
import net.sf.json.JSON;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestJiraConfig.class)
@TestPropertySource(locations = "classpath:test.properties")
@Ignore
/**
 * Some test methods if you want to play with local Jira
 */
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
        Issue issueByKey = jiraDao.getIssueByKey("CLAIM-5");
        System.out.println(issueByKey.getId());
    }

    @Test
    public void jiraCreateIssue() throws JiraException {
        jiraDao.createIssue(1000, 2, "partyo1", "party@mail.ru", "Head", "Description");
    }

    @Test
    public void closeJiraIssue() throws JiraException {
        jiraDao.closeIssue(1001, 2, "partyo1");
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
        JSON json = jiraDao.getJiraClient().getRestClient().delete("/rest/api/2/issue/CLAIM-1");
        System.out.printf(String.valueOf(json));

    }

    @Test
    public void testIgnore() {
        Claim claim = new Claim();
        Event event = new Event();
        EventSource eventSource = new EventSource();
        eventSource.setParty("39d17eca-0239-4ed8-8e32-dc78cf589135");
        event.setSource(eventSource);
        PartyEvent partyEvent = new PartyEvent();
        partyEvent.setClaimCreated(claim);

        EventPayload eventPayload = new EventPayload();
        eventPayload.setPartyEvent(partyEvent);
        event.setPayload(eventPayload);

        SourceEvent sourceEvent = new SourceEvent();
        sourceEvent.setProcessingEvent(event);
        StockEvent stockEvent = new StockEvent();
        stockEvent.setSourceEvent(sourceEvent);
        new PartyEventHandler().handle(stockEvent);
    }

}
