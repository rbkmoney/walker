package com.rbkmoney.walker.dao;

import com.rbkmoney.walker.config.JiraConfig;
import net.rcarz.jiraclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class JiraDao {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    JiraConfig config;


    private static final long delayMls = 1000;
    private static final long maxDelayMls = 10000;

    private final String REVOKE = "Revoke";
    private final String CLOSE = "Close";


    //library cant work in multithread mode
    private JiraClient getJiraClient() {
        BasicCredentials creds = new BasicCredentials(config.user_name, config.password);
        return new JiraClient(config.host, creds);
    }

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = delayMls, maxDelay = maxDelayMls), value = JiraException.class)
    public Issue getIssueByKey(String key) throws JiraException {
        JiraClient jira = getJiraClient();
        return jira.getIssue(key);
    }

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = delayMls, maxDelay = maxDelayMls), value = JiraException.class)
    public void createIssue(long eventId,
                            String claimId,
                            String partyId,
                            String summary,
                            String description) throws JiraException {
        JiraClient jira = getJiraClient();
        log.info("Try to create issue");
        Issue issue = jira.createIssue(config.PROJECT_KEY_NAME, config.ISSUE_TYPE_NAME)
                .field(Field.ASSIGNEE, config.user_name)
                .field(config.EVENT_ID, eventId)
                .field(config.CLAIM_ID, claimId)
                .field(config.PARTY_ID, partyId)
                .field(Field.SUMMARY, summary)
                .field(Field.DESCRIPTION, description)
                .execute();
        log.info("Created issue {}, ClaimdId {}", issue.getKey(), claimId);
    }

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = delayMls, maxDelay = maxDelayMls), value = JiraException.class)
    public void closeIssue(long eventId, String claimId) throws JiraException {
        log.info("Try to close issue");
        JiraClient jira = getJiraClient();
        Issue issue = jira.searchIssues("project =  WAL AND ClaimID ~ " + claimId, 1).issues.get(0);
        issue.update().field(config.EVENT_ID, eventId).execute();
        issue.transition().execute(CLOSE);
        log.info("Issue closed {}, ClaimId {}", issue.getKey(), claimId);
    }

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = delayMls, maxDelay = maxDelayMls), value = JiraException.class)
    public void closeRevokedIssue(long eventId, String claimId, String reason) throws JiraException {
        log.info("Try to close revokes issue");
        JiraClient jira = getJiraClient();
        Issue issue = jira.searchIssues("project =  WAL AND ClaimID ~ " + claimId, 1).issues.get(0);
        issue.update()
                .field(config.EVENT_ID, eventId)
                .field(config.REASON, "Revoked with reason: " + reason)
                .field(Field.ASSIGNEE, "walker")
                .execute();
        issue.transition().execute(REVOKE);
        log.info("Issue {} with ClaimID {} - revoked and closed", issue.getKey(), claimId);
    }

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = delayMls, maxDelay = maxDelayMls), value = JiraException.class)
    public void closeDeniedIssue(long eventId, String claimId, String reason) throws JiraException {
        log.info("Try to close denied issue");
        JiraClient jira = getJiraClient();
        Issue issue = jira.searchIssues("project =  WAL AND ClaimID ~ " + claimId, 1).issues.get(0);
        issue.update()
                .field(config.EVENT_ID, eventId)
                .field(config.REASON, "Denied with reason: " + reason)
                .field(Field.ASSIGNEE, "walker")
                .execute();
        issue.transition().execute(CLOSE);
        log.info("Issue {} with ClaimID {} - denied and closed", issue.getKey(), claimId);
    }

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = delayMls, maxDelay = maxDelayMls), value = JiraException.class)
    public long getLastEventId() throws JiraException {
        log.info("Try to get LastEventId from Jira host: {}", config.host);
        JiraClient jira = getJiraClient();
        Issue.SearchResult searchResult = jira.searchIssues("project =  WAL ORDER BY EvendID DESC", 1);
        Long lastEventId = 0L;
        if (!searchResult.issues.isEmpty()) {
            lastEventId = Math.round((Double) searchResult.issues.get(0).getField(config.EVENT_ID));
        }
        log.info("LastEventId in Jira is: {}", lastEventId);
        return lastEventId;
    }

    public Issue.SearchResult getFinishedIssues() throws JiraException {
        JiraClient jira = getJiraClient();
        return jira.searchIssues("project =  WAL AND status in ( Approved , Denied ) ORDER BY EvendID ", 100);
    }


    @Autowired
    private ApplicationContext appContext;

    @Recover
    public void recover(JiraException e) {
        log.error("Can't call Jira API host or use wrong method call. Tried for {} mls. Shutdown Walker application...", maxDelayMls, e);
        SpringApplication.exit(appContext, () -> -1);
    }

}
