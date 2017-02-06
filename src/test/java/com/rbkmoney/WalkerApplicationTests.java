package com.rbkmoney;

import com.rbkmoney.walker.WalkerApplication;
import com.rbkmoney.walker.service.JiraPoller;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WalkerApplication.class)
@Ignore
public class WalkerApplicationTests {

    @Autowired
    JiraPoller jiraPoller;

    @Test
    public void acceptIssues() {
        jiraPoller.pushFinishedIssuesToHG();
    }
}
