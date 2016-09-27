package com.rbkmoney;

import com.rbkmoney.walker.WalkerApplication;
import com.rbkmoney.walker.dao.JiraDao;
import net.rcarz.jiraclient.JiraException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WalkerApplication.class)
public class WalkerApplicationTests {

    @Autowired
    JiraDao jiraDao;

    @Test
    public void test() {
        System.out.println("test");
    }



    @Test
    public void jiraGetIssue() throws JiraException {
        jiraDao.getIssue("WAL-6");
    }

    @Test
    public void jiraCreateIssue() throws JiraException {
        jiraDao.createIssue(1000,"claim","useri","partyo","Head","Description");
    }

}
