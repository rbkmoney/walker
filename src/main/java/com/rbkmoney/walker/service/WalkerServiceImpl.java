package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.damsel.walker.*;
import com.rbkmoney.walker.dao.ActionDao;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.dao.CommentDao;
import com.rbkmoney.walker.domain.generated.tables.records.ActionRecord;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import com.rbkmoney.walker.domain.generated.tables.records.CommentRecord;
import com.rbkmoney.walker.utils.ThriftConvertor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.walker.utils.ThriftConvertor.convertToClaimInfo;
import static com.rbkmoney.walker.utils.ThriftConvertor.convertToHGPartyModification;
import static com.rbkmoney.walker.utils.TimeUtils.toIsoInstantString;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalkerServiceImpl implements WalkerSrv.Iface {

    private final ActionDao actionDao;
    private final PartyManagementSrv.Iface partyManagement;
    private final ClaimDao claimDao;
    private final CommentDao commentDao;

    @Override
    public void acceptClaim(String party_id, long claim_id, UserInformation user, int revision) throws InvalidUser, PartyNotFound, ClaimNotFound, InvalidClaimStatus, InvalidClaimRevision, InvalidChangeset, TException {
        log.info("Try to accept PartyId: {} , Claim with id: {}", party_id, claim_id);
        partyManagement.acceptClaim(buildUserInfo(user), party_id, claim_id, revision);
    }

    @Override
    public void denyClaim(String party_id, long claim_id, UserInformation user, String reason, int revision) throws InvalidUser, PartyNotFound, ClaimNotFound, InvalidClaimStatus, InvalidClaimRevision, TException {
        log.info("Try to deny PartyId: {} , Claim with id {}", party_id, claim_id);
        partyManagement.denyClaim(buildUserInfo(user), party_id, claim_id, revision, reason);
    }

    @Override
    public ClaimInfo getClaim(String party_id, long claim_id) throws ClaimNotFound, TException {
        log.info("Try to get Claim with id {}, PartyId: {}", claim_id, party_id);
        ClaimRecord claimRecord = claimDao.get(party_id, claim_id);
        //todo throw notfound
        if (claimRecord == null) {
            throw new ClaimNotFound();
        }
        try {
            return convertToClaimInfo(claimRecord);
        } catch (IOException e) {
            throw new TException(e);
        }
    }

    @Override
    public Claim createClaim(UserInformation user, String party_id, PartyModificationUnit changeset) throws InvalidUser, PartyNotFound, InvalidPartyStatus, ChangesetConflict, InvalidChangeset, InvalidRequest, TException {
        try {
            log.info("Try to create Claim with party_id: {} and user: {}", party_id, user.toString());
            return partyManagement.createClaim(buildUserInfo(user), party_id, convertToHGPartyModification(changeset));
        } catch (IOException e) {
            throw new TException(e);
        }
    }

    @Override
    public void updateClaim(String party_id, long claim_id, UserInformation user, PartyModificationUnit changeset, int revision) throws ClaimNotFound, InvalidClaimStatus, InvalidClaimRevision, ChangesetConflict, InvalidRequest, TException {
        try {
            log.info("Try to update Claim with id {} PartyId: {}", claim_id, party_id);
            partyManagement.updateClaim(buildUserInfo(user), party_id, claim_id, revision, convertToHGPartyModification(changeset));
        } catch (IOException e) {
            throw new TException(e);
        }
    }


    @Override
    public List<ClaimInfo> searchClaims(ClaimSearchRequest request) throws TException {
        try {
            log.info("Try to search Claim's {}", request);
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
    public void addComment(String party_id, long claim_id, UserInformation user, String text) throws TException {
        log.debug("Try to add comment to Claim with id: {}, PartyId: {}", claim_id, party_id);
        CommentRecord commentRecord = new CommentRecord();
        commentRecord.setPartyId(party_id);
        commentRecord.setText(text);
        commentRecord.setClaimId(claim_id);
        commentRecord.setUserId(user.getUserID());
        commentRecord.setUserName(user.getUserName());
        commentRecord.setEmail(user.getEmail());
        commentDao.add(commentRecord);
    }

    @Override
    public List<Comment> getComments(String party_id, long claim_id) throws TException {
        log.debug("Try to get comments to Claim with id {}, PartyId {}", claim_id, party_id);
        List<CommentRecord> comments = commentDao.getComments(party_id, claim_id);
        return comments.stream().map(cr -> {
            UserInformation userInformation = new UserInformation();
            userInformation.setEmail(cr.getEmail());
            userInformation.setUserID(cr.getUserId());
            userInformation.setUserName(cr.getUserName());
            Comment comment = new Comment();
            comment.setUser(userInformation);
            comment.setCreatedAt(toIsoInstantString(cr.getCreatedAt()));
            comment.setText(cr.getText());
            return comment;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Action> getActions(String party_id, long claim_id) throws TException {
        log.debug("Try to get actions to Claim with id {}, PartyId: {}", claim_id, party_id);
        List<ActionRecord> actionRecords = actionDao.getActions(party_id, claim_id);
        return actionRecords.stream().map(ThriftConvertor::convertToAction).collect(Collectors.toList());
    }

    private UserInfo buildUserInfo(UserInformation user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getUserID());
        userInfo.setType(UserType.internal_user(new InternalUser()));
        return userInfo;
    }
}
