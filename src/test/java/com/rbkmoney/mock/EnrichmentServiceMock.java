package com.rbkmoney.mock;

import com.rbkmoney.walker.service.EnrichmentService;
import org.apache.thrift.TException;

/**
 * @since 15.02.17
 **/
public class EnrichmentServiceMock implements EnrichmentService{

    @Override
    public String getPartyEmail(String partyId) throws TException {
        return "test@mail.ru";
    }
}
