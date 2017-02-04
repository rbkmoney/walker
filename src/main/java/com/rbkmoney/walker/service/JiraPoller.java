package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.walker.config.JiraConfig;
import com.rbkmoney.walker.dao.JiraDao;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.thrift.TException;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URI;

@Component
public class JiraPoller {

    @Autowired
    JiraDao jiraDao;

    @Autowired
    JiraConfig jiraConfig;

    @Autowired
    private PartyManagementSrv.Iface partyManagement;

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Scheduled(fixedDelay = 10000)
    public void pushFinishedIssuesToHG() {
        try {
            log.info("Pooling Jira for not processed issues. ");
            Issue.SearchResult finishedIssues = jiraDao.getFinishedIssues();
            for (Issue issue : finishedIssues.issues) {
                if (issue.getStatus().getName().equals(JiraConfig.APPROVED)) {
                    partyManagement.acceptClaim(
                            new UserInfo(issue.getAssignee().getId(), UserType.service_user(new ServiceUser())), //todo what id we need to keep in system?
                            String.valueOf(issue.getField(jiraConfig.PARTY_ID)),
                            (Long)issue.getField(jiraConfig.CLAIM_ID));
                    log.info("Accept claim in HG. Issue: {} ClaimID: {} ", issue.getKey(), issue.getField(jiraConfig.CLAIM_ID));
                } else if (issue.getStatus().getName().equals(JiraConfig.DENIED)) {
                    partyManagement.denyClaim(
                            new UserInfo(issue.getAssignee().getId(), UserType.service_user(new ServiceUser())), //todo what id we need to keep in system?
                            String.valueOf(issue.getField(jiraConfig.PARTY_ID)),
                            (Long)issue.getField(jiraConfig.CLAIM_ID),
                            String.valueOf(issue.getField(jiraConfig.REASON))
                    );
                    log.info("Deny claim in HG. Issue: {} ClaimID {} ", issue.getKey(), issue.getField(jiraConfig.CLAIM_ID));
                }
            }
        } catch (JiraException e) {
            log.error("Jira connection exception while pooling ", e);
        } catch (InvalidClaimStatus e) {
            log.warn("Invalid claim status exception. {}", e.getStatus().getFieldValue().toString());
        } catch (TException e) {
            log.error("Party Management service access error ", e);
        }
    }
}
