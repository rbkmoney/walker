package com.rbkmoney.walker.utils;

import com.bazaarvoice.jolt.JsonUtilImpl;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.payment_processing.PartyEvent;
import com.rbkmoney.damsel.payment_processing.PartyModification;
import com.rbkmoney.damsel.walker.*;
import com.rbkmoney.geck.serializer.kit.json.JsonHandler;
import com.rbkmoney.geck.serializer.kit.json.JsonProcessor;
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

import static com.rbkmoney.walker.utils.TimeUtils.toIsoInstantString;

/**
 * @since 23.03.17
 **/
public class ThriftConvertor {

    public static ObjectMapper mapper = new ObjectMapper();

    public static String convertToJson(TBase tBase) throws IOException {
        String json = new TBaseProcessor().process(tBase, new JsonHandler()).toString();
        return json;
    }

    public static PartyModificationUnit fromJsonPartyModificationUnit(String json) throws IOException {
        JsonNode jsonNode = mapper.readTree(json);
        return new JsonProcessor().process(jsonNode, new TBaseHandler<>(PartyModificationUnit.class));
    }

    public static PartyModification fromJsonPartyModification(String json) throws IOException {
        JsonNode jsonNode = mapper.readTree(json);
        return new JsonProcessor().process(jsonNode, new TBaseHandler<>(PartyModification.class));
    }


    public static PartyEvent fromJsonPartyEvent(String json) throws IOException {
        JsonNode jsonNode = mapper.readTree(json);
        return new JsonProcessor().process(jsonNode, new TBaseHandler<>(PartyEvent.class));
    }


    public static PartyModificationUnit convertToPartyModificationUnit(List<PartyModification> hgModifications) throws IOException {
        PartyModificationUnit partyModificationUnit = new PartyModificationUnit();
        partyModificationUnit.setModifications(hgModifications);
        return partyModificationUnit;
    }

    public static List<PartyModification> convertToHGPartyModification(PartyModificationUnit partyModificationUnit) throws IOException {
        return partyModificationUnit.getModifications();
    }

    public static Action convertToAction(ActionRecord actionRecord) {
        Action action = new Action();
        action.setCreatedAt(toIsoInstantString(actionRecord.getCreatedAt()));
        action.setUser(new UserInformation(actionRecord.getUserId()));
        action.setType(ActionType.valueOf(actionRecord.getType()));
        action.setAfter(actionRecord.getAfter());
        return action;
    }

    public static ClaimInfo convertToClaimInfo(ClaimRecord claimRecord) throws IOException {
        ClaimInfo claimInfo = new ClaimInfo();
        claimInfo.setPartyId(claimRecord.getPartyId());
        claimInfo.setClaimId(claimRecord.getId());
        claimInfo.setStatus(claimRecord.getStatus());
        claimInfo.setAssignedUserId(claimRecord.getAssignedUserId());
        claimInfo.setDescription(claimRecord.getDescription());
        claimInfo.setReason(claimRecord.getReason());
        claimInfo.setRevision(claimRecord.getRevision().toString());
        claimInfo.setCreatedAt(TimeUtils.toIsoInstantString(claimRecord.getCreatedAt()));
        claimInfo.setUpdatedAt(TimeUtils.toIsoInstantString(claimRecord.getUpdatedAt()));

        PartyModificationUnit partyModificationUnit = fromJsonPartyModificationUnit(String.valueOf(claimRecord.getChanges()));
        ;
        claimInfo.setModifications(partyModificationUnit);
        return claimInfo;
    }

}
