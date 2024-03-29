package com.rbkmoney.walker.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyModification;
import com.rbkmoney.damsel.walker.Action;
import com.rbkmoney.damsel.walker.ActionType;
import com.rbkmoney.damsel.walker.ClaimInfo;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.damsel.walker.UserInformation;
import com.rbkmoney.geck.serializer.kit.json.JsonHandler;
import com.rbkmoney.geck.serializer.kit.json.JsonProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import com.rbkmoney.walker.domain.generated.tables.records.ActionRecord;
import com.rbkmoney.walker.domain.generated.tables.records.ClaimRecord;
import org.apache.thrift.TBase;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.rbkmoney.walker.utils.TimeUtils.toIsoInstantString;

@SuppressWarnings({"ParameterName"})
public class ThriftConvertor {

    private static final ObjectMapper mapper = new ObjectMapper();

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

    public static PartyChange fromJsonPartyEvent(String json) throws IOException {
        JsonNode jsonNode = mapper.readTree(json);
        return new JsonProcessor().process(jsonNode, new TBaseHandler<>(PartyChange.class));
    }


    public static PartyModificationUnit convertToPartyModificationUnit(List<PartyModification> hgModifications)
            throws IOException {
        PartyModificationUnit partyModificationUnit = new PartyModificationUnit();
        partyModificationUnit.setModifications(
                !CollectionUtils.isEmpty(hgModifications) ? hgModifications : new ArrayList<>());
        return partyModificationUnit;
    }

    public static List<PartyModification> convertToHgPartyModification(PartyModificationUnit partyModificationUnit)
            throws IOException {
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

        PartyModificationUnit partyModificationUnit =
                fromJsonPartyModificationUnit(String.valueOf(claimRecord.getChanges()));
        claimInfo.setModifications(partyModificationUnit);
        return claimInfo;
    }

}
