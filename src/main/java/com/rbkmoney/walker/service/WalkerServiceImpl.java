package com.rbkmoney.walker.service;

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
    ActionDao actionDao;

    @Override
    public void approveClaim(long claimID) throws TException {

    }

    @Override
    public void declineClaim(long claimID, UserInfo user, String reason) throws TException {

    }

    @Override
    public ClaimInfo getClaim(long claimID, UserInfo user) throws TException {
        return null;
    }

    @Override
    public void createClaim(UserInfo user, String party_id, PartyModificationUnit changeset) throws TException {

    }

    @Override
    public void updateClaim(long claimID, UserInfo user, PartyModificationUnit changeset) throws TException {

    }

    @Override
    public List<ClaimInfo> searchClaims(ClaimSearchRequest request) throws TException {
        return null;
    }

    @Override
    public void addComment(long claimId, UserInfo user, String text) throws TException {

    }

    @Override
    public List<Comment> getComments(long claimId, UserInfo user) throws TException {
        return null;
    }

    @Override
    public List<Action> getActions(long claimId, UserInfo user) throws TException {
        return null;
    }
}
