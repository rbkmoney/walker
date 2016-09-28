package com.rbkmoney.walker.handler.poller;

import com.rbkmoney.damsel.payment_processing.InvalidClaimStatus;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.damsel.payment_processing.UserInfo;
import com.rbkmoney.walker.config.JiraConfig;
import com.rbkmoney.walker.dao.JiraDao;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
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

    @Value("${hg.party.management.url}")
    private String PARTY_MANAGEMENT_SERVICE_URL;

    private PartyManagementSrv.Iface partyManagement;
    Logger log = LoggerFactory.getLogger(this.getClass());


    @PostConstruct
    public void setUp() throws Exception {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withHttpClient(HttpClientBuilder.create().build())
                .withAddress(new URI(PARTY_MANAGEMENT_SERVICE_URL));
        partyManagement = clientBuilder.build(PartyManagementSrv.Iface.class);
    }

    @Scheduled(fixedDelay = 10000)
    public void pushFinishedIssuesToHG() {
        try {
            log.info("Pooling Jira for not processed issues. ");
            Issue.SearchResult finishedIssues = jiraDao.getFinishedIssues();
            finishedIssues.issues.stream().forEach(issue -> {
                if (issue.getStatus().getName().equals(JiraConfig.APPROVED))
                    try {
                        partyManagement.acceptClaim(
                                new UserInfo(issue.getAssignee().getName()), //todo what id we need to keep in system?
                                String.valueOf(issue.getField(jiraConfig.PARTY_ID)),
                                String.valueOf(issue.getField(jiraConfig.CLAIM_ID)));
                    } catch (TException e) {
                        e.printStackTrace();
                    }
            });


        } catch (JiraException e) {
            e.printStackTrace();
        }
    }
}
