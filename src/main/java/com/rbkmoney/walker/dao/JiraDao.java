package com.rbkmoney.walker.dao;

import com.rbkmoney.walker.config.JiraConfig;
import net.rcarz.jiraclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class JiraDao {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    JiraConfig config;

    private JiraClient jira;

    @PostConstruct
    public void init() {
        BasicCredentials creds = new BasicCredentials(config.user_name, config.password);
        jira = new JiraClient(config.host, creds);

    }

    public Issue getIssueByKey(String key) throws JiraException {
        return jira.getIssue(key);
    }

    public void createIssue(long eventId,
                            String claimId,
                            String partyId,
                            String summary,
                            String description) throws JiraException {
        Issue issue = jira.createIssue(config.PROJECT_KEY_NAME, config.ISSUE_TYPE_NAME)
                .field(Field.ASSIGNEE, config.user_name)
                .field(config.EVENT_ID, eventId)
                .field(config.CLAIM_ID, claimId)
                .field(config.PARTY_ID, partyId)
                .field(config.SUMMARY, summary)
                .field(Field.DESCRIPTION, description)
                .execute();
        log.info("Created issue {}", issue.getKey());
    }

    public void closeIssue(String claimId) throws JiraException {
        Issue issue = jira.searchIssues("project =  WAL AND ClaimID ~ " + claimId, 1).issues.get(0);
        issue.transition().execute("Close");
        log.info("Issue closed {}, ClaimId {}", issue.getKey(), claimId);
    }

    public synchronized void closeRevokedIssue(String claimId, String reason) throws JiraException {
        Issue issue = jira.searchIssues("project =  WAL AND ClaimID ~ " + claimId, 1).issues.get(0);
        issue.update()
                .field(config.REASON, "Revoked with reason: " + reason)
                .field(Field.ASSIGNEE, "walker")
                .execute();
        issue.transition().execute("Revoke");
        log.info("Issue {} - revoked and closed", issue.getKey());
    }


    public Issue.SearchResult getFinishedIssues() throws JiraException {
        return jira.searchIssues("project =  WAL AND status in ( Approved , Denied ) ORDER BY EvendID ", 100);
    }

    public long getLastEventId() throws JiraException {
        Issue.SearchResult searchResult = jira.searchIssues("project =  WAL ORDER BY EvendID DESC", 1);
        Long lastEventId = 0L;
        if (!searchResult.issues.isEmpty()) {
            lastEventId = Math.round((Double) searchResult.issues.get(0).getField(config.EVENT_ID));
        }
        log.info("Last processed eventId in Jira is: {}", lastEventId);
        return lastEventId;
    }
}
