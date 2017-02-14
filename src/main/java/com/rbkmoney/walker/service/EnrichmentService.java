package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.domain.Party;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.damsel.payment_processing.ServiceUser;
import com.rbkmoney.damsel.payment_processing.UserInfo;
import com.rbkmoney.damsel.payment_processing.UserType;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Add data from remote services
 *
 * @since 14.02.17
 **/
@Component
public class EnrichmentService {

    private static String userId = "walker";

    @Autowired
    private PartyManagementSrv.Iface partyManagement;

    public String getPartyEmail(String partyId) throws TException {
        Party party = partyManagement.get(new UserInfo(userId, UserType.service_user(new ServiceUser())), partyId);
        return party.getContactInfo().getEmail();
    }
}
