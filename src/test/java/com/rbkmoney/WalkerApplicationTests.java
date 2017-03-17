package com.rbkmoney;

import com.rbkmoney.walker.WalkerApplication;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WalkerApplication.class)
public class WalkerApplicationTests {


    @Test
    public void simple(){
        //nothing
    }

}
