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

import java.util.List;

import static com.rbkmoney.walker.config.JiraConfig.*;

@Service
public class JiraDao {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    JiraConfig config;

    private final long maxDelayMls = 15 * 60 * 1000;

    public List<IssueType> getTypes() throws JiraException {
        JiraClient jira = getJiraClient();
        return jira.getIssueTypes();
    }

    @Retryable(maxAttempts = 20, backoff = @Backoff(multiplier = 2, maxDelay = maxDelayMls), value = JiraException.class)
    public Issue getIssueByKey(String key) throws JiraException {
        JiraClient jira = getJiraClient();
        return jira.getIssue(key);
    }

    @Retryable(maxAttempts = 20, backoff = @Backoff(multiplier = 2, maxDelay = maxDelayMls), value = JiraException.class)
    public void createIssue(long eventId,
                            long claimId,
                            String partyId,
                            String email,
                            String summary,
                            String description) throws JiraException {
        JiraClient jira = getJiraClient();
        log.info("Try to create issue with ClaimID: {}", claimId);
        Issue issue = jira.createIssue(config.PROJECT_KEY_NAME, config.ISSUE_TYPE_NAME)
                .field(Field.ASSIGNEE, config.user_name)
                .field(config.EVENT_ID, eventId)
                .field(config.CLAIM_ID, claimId)
                .field(config.PARTY_ID, partyId)
                .field(config.EMAIL, email)
                .field(Field.SUMMARY, summary)
                .field(Field.DESCRIPTION, description)
                .execute();
        log.info("Created issue {}, ClaimID: {}", issue.getKey(), claimId);
    }

    @Retryable(maxAttempts = 20, backoff = @Backoff(multiplier = 2, maxDelay = maxDelayMls), value = JiraException.class)
    public void closeIssue(long eventId, long claimId, String partyId) throws JiraException {
        log.info("Try to close issue");
        Issue issue = getIssueByClaimAndPartyId(claimId, partyId);
        issue.update().field(config.EVENT_ID, eventId).execute();
        issue.transition().execute(CLOSE);
        log.info("Issue closed {}, ClaimId {}", issue.getKey(), claimId);
    }

    @Retryable(maxAttempts = 20, backoff = @Backoff(multiplier = 2, maxDelay = maxDelayMls), value = JiraException.class)
    public void closeRevokedIssue(long eventId, long claimId, String partyId, String reason) throws JiraException {
        log.info("Try to close revoked issue with ClaimID: {}", claimId);
        Issue issue = getIssueByClaimAndPartyId(claimId, partyId);
        issue.update()
                .field(config.EVENT_ID, eventId)
                .field(config.REASON, "Revoked with reason: " + reason)
                .field(Field.ASSIGNEE, "walker")
                .execute();
        issue.transition().execute(REVOKE);
        log.info("Issue {} with ClaimID {} - revoked and closed", issue.getKey(), claimId);
    }

    @Retryable(maxAttempts = 20, backoff = @Backoff(multiplier = 2, maxDelay = maxDelayMls), value = JiraException.class)
    public void closeDeniedIssue(long eventId, long claimId, String partyId, String reason) throws JiraException {
        log.info("Try to close denied issue with ClaimID: ", claimId);
        Issue issue = getIssueByClaimAndPartyId(claimId, partyId);
        issue.update()
                .field(config.EVENT_ID, eventId)
                .field(config.REASON, "Denied with reason: " + reason)
                .field(Field.ASSIGNEE, "walker")
                .execute();
        issue.transition().execute(CLOSE);
        log.info("Issue {} with ClaimID {} - denied and closed", issue.getKey(), claimId);
    }

    public long getLastEventId() throws JiraException {
        log.info("Try to get LastEventId from Jira host: {}", config.host);
        JiraClient jira = getJiraClient();
        Issue.SearchResult searchResult = jira.searchIssues("project = " + config.PROJECT_KEY_NAME + " ORDER BY EventID DESC", 1);
        Long lastEventId = 0L;
        if (!searchResult.issues.isEmpty()) {
            lastEventId = Math.round((Double) searchResult.issues.get(0).getField(config.EVENT_ID));
        }
        log.info("LastEventId in Jira is: {}", lastEventId);
        return lastEventId;
    }

    public Issue.SearchResult getFinishedIssues() throws JiraException {
        JiraClient jira = getJiraClient();
        return jira.searchIssues("project =  " + config.PROJECT_KEY_NAME
                + " AND status in ( " + APPROVED + " , " + DENIED + " ) ORDER BY EventID ", 100);
    }

    private Issue getIssueByClaimAndPartyId(long claimId, String partyId) throws JiraException {
        JiraClient jira = getJiraClient();
        List<Issue> issues = jira.searchIssues(
                "project = " + config.PROJECT_KEY_NAME + " AND PartyID ~ " + partyId + " AND ClaimID ~ " + claimId, 1).issues;
        if (issues.isEmpty()) {
            throw new JiraException("Cant get issue from Jira with ClaimID " + claimId + " and PartyId " + partyId);
        }
        return issues.get(0);
    }

    //library cant work in multithread mode
    public JiraClient getJiraClient() {
        BasicCredentials creds = new BasicCredentials(config.user_name, config.password);
        return new JiraClient(config.host, creds);
    }


    @Autowired
    private ApplicationContext appContext;

    @Recover
    private void recover(JiraException e) {
        //todo: will be fixed in different pool request
        log.error("Can't call Jira API host or use wrong method call. Tried for {} mls. Shutdown Walker application...", maxDelayMls, e);
        SpringApplication.exit(appContext, () -> -1);
    }

}
