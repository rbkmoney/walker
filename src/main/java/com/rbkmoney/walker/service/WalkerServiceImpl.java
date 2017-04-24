package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.payment_processing.InternalUser;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.damsel.payment_processing.UserInfo;
import com.rbkmoney.damsel.payment_processing.UserType;
import com.rbkmoney.damsel.walker.*;
import com.rbkmoney.damsel.walker.Action;
import com.rbkmoney.damsel.walker.Comment;
import com.rbkmoney.walker.dao.ActionDao;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.dao.CommentDao;
import com.rbkmoney.walker.domain.generated.tables.records.ActionRecord;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import com.rbkmoney.walker.domain.generated.tables.records.CommentRecord;
import com.rbkmoney.walker.utils.ThriftConvertor;
import com.rbkmoney.walker.utils.TimeUtils;
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

/**
 * @since 15.03.17
 **/
@Service
public class WalkerServiceImpl implements WalkerSrv.Iface {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ActionDao actionDao;

    @Autowired
    private PartyManagementSrv.Iface partyManagement;

    @Autowired
    private ClaimDao claimDao;


    @Autowired
    private CommentDao commentDao;

    @Override
    public void acceptClaim(long claimID, UserInformation user, int revision) throws TException {
        log.info("Try to accept Claim with id {}", claimID);
        partyManagement.acceptClaim(buildUserInfo(user), user.getUserID(), claimID, revision);
    }

    @Override
    public void denyClaim(long claimID, UserInformation user, String reason, int revision) throws TException {
        log.info("Try to deny Claim with id {}", claimID);
        partyManagement.denyClaim(buildUserInfo(user), user.getUserID(), claimID, revision, reason);
    }

    @Override
    public ClaimInfo getClaim(long claim_id) throws ClaimNotFound, TException {
        log.info("Try to get Claim with id {}", claim_id);
        ClaimRecord claimRecord = claimDao.get(claim_id);
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
    public void createClaim(UserInformation user, String party_id, PartyModificationUnit changeset) throws TException {
        try {
            log.info("Try to create Claim with party id {}", party_id);
            partyManagement.createClaim(buildUserInfo(user), party_id, convertToHGPartyModification(changeset));
        } catch (IOException e) {
            throw new TException(e);
        }
    }

    @Override
    public void updateClaim(long claimID, UserInformation user, PartyModificationUnit changeset, int revision) throws TException {
        try {
            log.info("Try to update Claim with id {}", claimID);
            partyManagement.updateClaim(buildUserInfo(user), user.getUserID(), claimID, revision, convertToHGPartyModification(changeset));
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
    public void addComment(long claimId, UserInformation user, String text) throws TException {
        log.debug("Try to add comment to Claim with id {}", claimId);
        CommentRecord commentRecord = new CommentRecord();
        commentRecord.setText(text);
        commentRecord.setClaimId(claimId);
        commentRecord.setUserId(user.getUserID());
        commentRecord.setUserName(user.getUserName());
        commentRecord.setEmail(user.getEmail());
        commentDao.add(commentRecord);
    }

    @Override
    public List<Comment> getComments(long claim_id) throws TException {
        log.debug("Try to get comments to Claim with id {}", claim_id);
        List<CommentRecord> comments = commentDao.getComments(claim_id);
        return comments.stream().map(cr -> {
            UserInformation userInformation = new UserInformation();
            userInformation.setEmail(cr.getEmail());
            userInformation.setUserID(cr.getUserId());
            userInformation.setUserName(cr.getUserName());
            Comment comment = new Comment();
            comment.setUser(userInformation);
            comment.setCreatedAt(TimeUtils.timestampToString(cr.getCreatedAt()));
            comment.setText(cr.getText());
            return comment;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Action> getActions(long claim_id) throws TException {
        log.debug("Try to get actions to Claim with id {}", claim_id);
        List<ActionRecord> actionRecords = actionDao.getActionsByClaimId(claim_id);
        return actionRecords.stream().map(ThriftConvertor::convertToAction).collect(Collectors.toList());
    }

    private UserInfo buildUserInfo(UserInformation user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getUserID());
        userInfo.setType(UserType.internal_user(new InternalUser()));
        return userInfo;
    }
}
