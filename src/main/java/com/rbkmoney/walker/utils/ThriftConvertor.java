package com.rbkmoney.walker.utils;

import com.bazaarvoice.jolt.JsonUtilImpl;
import com.bazaarvoice.jolt.JsonUtils;
import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.payment_processing.PartyModification;
import com.rbkmoney.damsel.walker.*;
import com.rbkmoney.geck.serializer.kit.object.ObjectHandler;
import com.rbkmoney.geck.serializer.kit.object.ObjectProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import com.rbkmoney.walker.domain.generated.tables.records.ActionRecord;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import org.apache.thrift.TBase;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @since 23.03.17
 **/
public class ThriftConvertor {

    public static String convertToJson(PartyModificationUnit partyModificationUnit) throws IOException {
        Object object = new TBaseProcessor().process(partyModificationUnit, new ObjectHandler());
        return JsonUtils.toJsonString(object);
    }

    public static String convertToJson(TBase claimStatus) throws IOException {
        Object object = new TBaseProcessor().process(claimStatus, new ObjectHandler());
        return JsonUtils.toJsonString(object);
    }


    public static PartyModificationUnit convertToPartyModificationUnit(List<PartyModification> hgModifications) throws IOException {
        LinkedList<com.rbkmoney.damsel.walker.PartyModification> walkerPartyModificationList = new LinkedList<>();
        for (PartyModification hgModification : hgModifications) {
            com.rbkmoney.damsel.walker.PartyModification partyModification = convertToWalkerModification(hgModification);
            walkerPartyModificationList.add(partyModification);
        }
        PartyModificationUnit partyModificationUnit = new PartyModificationUnit();
        partyModificationUnit.setModifications(walkerPartyModificationList);
        return partyModificationUnit;
    }

    /**
     * Convert from Payment_processing thrift object to Walker thrift representation
     */
    public static com.rbkmoney.damsel.walker.PartyModification convertToWalkerModification(PartyModification hgModification) throws IOException {
        Object hgModifObj = new TBaseProcessor().process(hgModification, new ObjectHandler());
        String hgJson = JsonUtils.toJsonString(hgModifObj);
        Object objFromJson = new JsonUtilImpl().jsonToObject(hgJson);
        return new ObjectProcessor()
                .process(objFromJson, new TBaseHandler<>(com.rbkmoney.damsel.walker.PartyModification.class));
    }

    public static List<PartyModification> convertToHGPartyModification(PartyModificationUnit partyModificationUnit) throws IOException {
        LinkedList<PartyModification> partyModifications = new LinkedList<>();
        //looks like its very slow ...
        for (com.rbkmoney.damsel.walker.PartyModification modification : partyModificationUnit.getModifications()) {
            Object modifObj = new TBaseProcessor().process(modification, new ObjectHandler());
            String modifObjJson = JsonUtils.toJsonString(modifObj);
            Object objFromJson = new JsonUtilImpl().jsonToObject(modifObjJson);
            PartyModification hgModification = new ObjectProcessor()
                    .process(objFromJson, new TBaseHandler<>(PartyModification.class));
            partyModifications.add(hgModification);
        }
        return partyModifications;
    }

    public static Action convertToAction(ActionRecord actionRecord) {
        Action action = new Action();
        action.setCreatedAt(TimeUtils.timestampToString(actionRecord.getCreatedAt()));
        action.setUser(new UserInformation(actionRecord.getUserId()));
        action.setType(ActionType.valueOf(actionRecord.getType()));
        action.setAfter(actionRecord.getAfter());
        return action;
    }

    public static ClaimInfo convertToClaimInfo(ClaimRecord claimRecord) throws IOException {
        ClaimInfo claimInfo = new ClaimInfo();
        claimInfo.setClaimId(claimRecord.getId());
        claimInfo.setStatus(claimRecord.getStatus());
        claimInfo.setAssignedUserId(claimRecord.getAssignedUserId());
        claimInfo.setDescription(claimRecord.getDescription());
        claimInfo.setReason(claimRecord.getReason());
        claimInfo.setRevision(claimRecord.getRevision().toString());

        Object objFromJson = new JsonUtilImpl().jsonToObject(String.valueOf(claimRecord.getChanges()));
        PartyModificationUnit partyModificationUnit = new ObjectProcessor().process(objFromJson, new TBaseHandler<>(PartyModificationUnit.class));
        claimInfo.setModifications(partyModificationUnit);
        return claimInfo;
    }

}
