package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.domain.Party;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.damsel.payment_processing.ServiceUser;
import com.rbkmoney.damsel.payment_processing.UserInfo;
import com.rbkmoney.damsel.payment_processing.UserType;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @since 15.02.17
 **/
@Service
public class EnrichmentServiceImpl implements EnrichmentService {

    private static String userId = "walker";

    @Autowired
    private PartyManagementSrv.Iface partyManagement;

    @Override
    public String getPartyEmail(String partyId) throws TException {
        Party party = partyManagement.get(new UserInfo(userId, UserType.service_user(new ServiceUser())), partyId);
        return party.getContactInfo().getEmail();
    }
}
