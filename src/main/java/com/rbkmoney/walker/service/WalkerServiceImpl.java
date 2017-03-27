package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.damsel.walker.*;
import com.rbkmoney.walker.dao.ActionDao;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @since 15.03.17
 **/
@Service
public class WalkerServiceImpl implements WalkerSrv.Iface {

    @Autowired
    private ActionDao actionDao;

    @Autowired
    private PartyManagementSrv.Iface partyManagement;


    @Override
    public void acceptClaim(long claimID, UserInformation user, int revision) throws TException {

    }

    @Override
    public void denyClaim(long claimID, UserInformation user, String reason, int revision) throws TException {

    }

    @Override
    public ClaimInfo getClaim(long claimID, UserInformation user) throws TException {
        return null;
    }

    @Override
    public void createClaim(UserInformation user, String party_id, PartyModificationUnit changeset) throws TException {

    }

    @Override
    public void updateClaim(long claimID, UserInformation user, PartyModificationUnit changeset, int revision) throws TException {

    }

    @Override
    public List<ClaimInfo> searchClaims(ClaimSearchRequest request) throws TException {
        return null;
    }

    @Override
    public void addComment(long claimId, UserInformation user, String text) throws TException {

    }

    @Override
    public List<Comment> getComments(long claimId, UserInformation user) throws TException {
        return null;
    }

    @Override
    public List<Action> getActions(long claimId, UserInformation user) throws TException {
        return null;
    }
}
