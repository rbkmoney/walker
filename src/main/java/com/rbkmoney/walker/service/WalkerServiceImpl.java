package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.payment_processing.InternalUser;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.damsel.payment_processing.UserInfo;
import com.rbkmoney.damsel.payment_processing.UserType;
import com.rbkmoney.damsel.walker.*;
import com.rbkmoney.walker.dao.ActionDao;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.domain.generated.tables.records.ActionRecord;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import com.rbkmoney.walker.utils.ThriftConvertor;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.walker.utils.ThriftConvertor.convertToAction;
import static com.rbkmoney.walker.utils.ThriftConvertor.convertToClaimInfo;
import static com.rbkmoney.walker.utils.ThriftConvertor.convertToHGPartyModification;

/**
 * @since 15.03.17
 **/
@Service
public class WalkerServiceImpl implements WalkerSrv.Iface {

    @Autowired
    private ActionDao actionDao;

    @Autowired
    private PartyManagementSrv.Iface partyManagement;

    @Autowired
    private ClaimDao claimDao;


    @Override
    public void acceptClaim(long claimID, UserInformation user, int revision) throws TException {
        partyManagement.acceptClaim(buildUserInfo(user), user.getUserID(), claimID, revision);
    }

    @Override
    public void denyClaim(long claimID, UserInformation user, String reason, int revision) throws TException {
        partyManagement.denyClaim(buildUserInfo(user), user.getUserID(), claimID, revision, reason);
    }

    @Override
    public ClaimInfo getClaim(long claimID, UserInformation user) throws TException {
        ClaimRecord claimRecord = claimDao.get(claimID);
        try {
            return convertToClaimInfo(claimRecord);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void createClaim(UserInformation user, String party_id, PartyModificationUnit changeset) throws TException {
        try {
            partyManagement.createClaim(buildUserInfo(user), party_id, convertToHGPartyModification(changeset));
        } catch (IOException e) {
            //todo: add correct exception to interface
            e.printStackTrace();
        }
    }

    @Override
    public void updateClaim(long claimID, UserInformation user, PartyModificationUnit changeset, int revision) throws TException {
        try {
            partyManagement.updateClaim(buildUserInfo(user), user.getUserID(), claimID, revision, convertToHGPartyModification(changeset));
        } catch (IOException e) {
            //todo: add correct exception to interface
            e.printStackTrace();
        }
    }

    @Override
    public List<ClaimInfo> searchClaims(ClaimSearchRequest request) throws TException {
        try {
            List<ClaimRecord> searchResult = claimDao.search(request);
            LinkedList<ClaimInfo> result = new LinkedList<ClaimInfo>();
            for (ClaimRecord claimRecord : searchResult) {
                result.add(ThriftConvertor.convertToClaimInfo(claimRecord));
            }
            return result;
        } catch (IOException e) {
            throw new TException(e);
        }
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
        List<ActionRecord> actionRecords = actionDao.getActionsByClaimId(claimId);
        return actionRecords.stream().map(ThriftConvertor::convertToAction).collect(Collectors.toList());
    }


    private UserInfo buildUserInfo(UserInformation user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getUserID());
        userInfo.setType(UserType.internal_user(new InternalUser()));
        return userInfo;
    }
}
