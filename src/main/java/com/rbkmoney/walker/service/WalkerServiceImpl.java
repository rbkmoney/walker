package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.payment_processing.ChangesetConflict;
import com.rbkmoney.damsel.payment_processing.Claim;
import com.rbkmoney.damsel.payment_processing.ClaimNotFound;
import com.rbkmoney.damsel.payment_processing.InternalUser;
import com.rbkmoney.damsel.payment_processing.InvalidChangeset;
import com.rbkmoney.damsel.payment_processing.InvalidClaimRevision;
import com.rbkmoney.damsel.payment_processing.InvalidClaimStatus;
import com.rbkmoney.damsel.payment_processing.InvalidPartyStatus;
import com.rbkmoney.damsel.payment_processing.InvalidUser;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.damsel.payment_processing.PartyNotFound;
import com.rbkmoney.damsel.payment_processing.UserInfo;
import com.rbkmoney.damsel.payment_processing.UserType;
import com.rbkmoney.damsel.walker.Action;
import com.rbkmoney.damsel.walker.ClaimInfo;
import com.rbkmoney.damsel.walker.ClaimSearchRequest;
import com.rbkmoney.damsel.walker.Comment;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.damsel.walker.UserInformation;
import com.rbkmoney.damsel.walker.WalkerSrv;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.walker.utils.ThriftConvertor.convertToClaimInfo;
import static com.rbkmoney.walker.utils.ThriftConvertor.convertToHgPartyModification;
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
    public void acceptClaim(String partyId, long claimId, UserInformation user, int revision)
            throws TException {
        log.info("Try to accept PartyId: {} , Claim with id: {}", partyId, claimId);
        partyManagement.acceptClaim(buildUserInfo(user), partyId, claimId, revision);
    }

    @Override
    public void denyClaim(String partyId, long claimId, UserInformation user, String reason, int revision)
            throws TException {
        log.info("Try to deny PartyId: {} , Claim with id {}", partyId, claimId);
        partyManagement.denyClaim(buildUserInfo(user), partyId, claimId, revision, reason);
    }

    @Override
    public ClaimInfo getClaim(String partyId, long claimId) throws TException {
        log.info("Try to get Claim with id {}, PartyId: {}", claimId, partyId);
        ClaimRecord claimRecord = claimDao.get(partyId, claimId);
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
    public Claim createClaim(UserInformation user, String partyId, PartyModificationUnit changeset)
            throws
            TException {
        try {
            log.info("Try to create Claim with party_id: {} and user: {}", partyId, user.toString());
            return partyManagement.createClaim(buildUserInfo(user), partyId, convertToHgPartyModification(changeset));
        } catch (IOException e) {
            throw new TException(e);
        }
    }

    @Override
    public void updateClaim(String partyId, long claimId, UserInformation user, PartyModificationUnit changeset,
                            int revision)
            throws TException {
        try {
            log.info("Try to update Claim with id {} PartyId: {}", claimId, partyId);
            partyManagement.updateClaim(buildUserInfo(user), partyId, claimId, revision,
                    convertToHgPartyModification(changeset));
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
    public void addComment(String partyId, long claimId, UserInformation user, String text) throws TException {
        log.debug("Try to add comment to Claim with id: {}, PartyId: {}", claimId, partyId);
        CommentRecord commentRecord = new CommentRecord();
        commentRecord.setPartyId(partyId);
        commentRecord.setText(text);
        commentRecord.setClaimId(claimId);
        commentRecord.setUserId(user.getUserID());
        commentRecord.setUserName(user.getUserName());
        commentRecord.setEmail(user.getEmail());
        commentDao.add(commentRecord);
    }

    @Override
    public List<Comment> getComments(String partyId, long claimId) throws TException {
        log.debug("Try to get comments to Claim with id {}, PartyId {}", claimId, partyId);
        List<CommentRecord> comments = commentDao.getComments(partyId, claimId);
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
    public List<Action> getActions(String partyId, long claimId) throws TException {
        log.debug("Try to get actions to Claim with id {}, PartyId: {}", claimId, partyId);
        List<ActionRecord> actionRecords = actionDao.getActions(partyId, claimId);
        return actionRecords.stream().map(ThriftConvertor::convertToAction).collect(Collectors.toList());
    }

    private UserInfo buildUserInfo(UserInformation user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getUserID());
        userInfo.setType(UserType.internal_user(new InternalUser()));
        return userInfo;
    }
}
