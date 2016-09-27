package com.rbkmoney.walker.dao;

import com.rbkmoney.walker.config.JIraConfig;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class JiraDao {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private JiraClient jira;

    @Autowired
    JIraConfig config;

    @PostConstruct
    public void init() {
        BasicCredentials creds = new BasicCredentials(config.user_name, config.password);
        jira = new JiraClient(config.host, creds);

    }

    public void createIssue(long eventId,
                            String claimId,
                            String userId,
                            String partyId,
                            String summary,
                            String description) throws JiraException {
        jira.createIssue(config.PROJECT_KEY_NAME, config.ISSUE_TYPE_NAME)
                .field(config.ASSIGNEE, config.user_name)
                .field(config.EVENT_ID, eventId)
                .field(config.CLAIM_ID, claimId)
                .field(config.USER_ID, userId)
                .field(config.PARTY_ID, partyId)
                .field(config.SUMMARY, summary)
                .field(config.DESCRIPTION, description)
                .execute();
    }

    public void getIssue(String issueKey) throws JiraException {
        Issue issue = jira.getIssue(issueKey);
        System.out.println(issue.getId());
    }

    public long getLastEventId() {
        //todo:
        return 0L;
    }
}
