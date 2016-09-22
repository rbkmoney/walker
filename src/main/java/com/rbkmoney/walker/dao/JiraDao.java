package com.rbkmoney.walker.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JiraDao {

    @Value("${jira.api.url}")
    private String host;

    public void auth(){
        String AUTH = "/rest/auth/1/session";

    }

    public void getLastIdTask(){

    }
    public void createTask(){}

    public void cancelTask(){}

    public void closeTask(){}
}
