package com.rbkmoney.walker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JIraConfig {

    @Value("${jira.api.url}")
    public String host;

    @Value("${jira.user.name}")
    public String user_name;

    @Value("${jira.user.password}")
    public String password;



    @Value("${ISSUE_TYPE_NAME}")
    public String ISSUE_TYPE_NAME;

    @Value("${PROJECT_KEY_NAME}")
    public String PROJECT_KEY_NAME;


    @Value("${RESOLUTION}")
    public String RESOLUTION;


    @Value("${SUMMARY}")
    public String SUMMARY;


    @Value("${CLAIM_ID}")
    public String CLAIM_ID;


    @Value("${EVENT_ID}")
    public String EVENT_ID;


    @Value("${USER_ID}")
    public String USER_ID;


    @Value("${PARTY_ID}")
    public String PARTY_ID;

    public final String DESCRIPTION = "description";
    public final String ASSIGNEE = "assignee";


}
