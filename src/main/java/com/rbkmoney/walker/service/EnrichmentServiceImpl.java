package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.domain.Party;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.damsel.payment_processing.ServiceUser;
import com.rbkmoney.damsel.payment_processing.UserInfo;
import com.rbkmoney.damsel.payment_processing.UserType;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrichmentServiceImpl implements EnrichmentService {

    private static final String USER_ID = "walker";
    private static final UserInfo USER_INFO = new UserInfo(USER_ID, UserType.service_user(new ServiceUser()));
    private final PartyManagementSrv.Iface partyManagement;

    @Override
    public String getPartyEmail(String partyId) throws TException {
        Party party = partyManagement.get(USER_INFO, partyId);
        return party.getContactInfo().getEmail();
    }
}
