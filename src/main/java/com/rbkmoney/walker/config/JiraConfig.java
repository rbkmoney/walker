package com.rbkmoney.walker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JiraConfig {
    @Value("${jira.api.url}")
    public String host;

    @Value("${JIRA_USER}")
    public String user_name;

    @Value("${JIRA_PASSWORD}")
    public String password;

    @Value("${jira.issue.type.name}")
    public String ISSUE_TYPE_NAME;

    @Value("${jira.project_key.name}")
    public String PROJECT_KEY_NAME;

    @Value("${jira.claim_id.field}")
    public String CLAIM_ID;

    @Value("${jira.even_id.field}")
    public String EVENT_ID;

    @Value("${jira.party_id.field}")
    public String PARTY_ID;

    @Value("${jira.reason.field}")
    public String REASON;

    //Jira state names
    public final static String APPROVED = "Approved";
    public final static String DENIED = "Denied";

    //Jira transition names
    public final static String REVOKE = "Revoke";
    public final static String CLOSE = "Close";


}
