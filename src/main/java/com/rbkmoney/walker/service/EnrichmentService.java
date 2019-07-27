package com.rbkmoney.walker.service;

import org.apache.thrift.TException;

public interface EnrichmentService {

    String getPartyEmail(String partyId) throws TException;
}
