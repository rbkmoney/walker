package com.rbkmoney.walker.service;

import org.apache.thrift.TException;

/**
 * Add data from remote services
 *
 * @since 14.02.17
 **/
public interface EnrichmentService {

    String getPartyEmail(String partyId) throws TException;
}
