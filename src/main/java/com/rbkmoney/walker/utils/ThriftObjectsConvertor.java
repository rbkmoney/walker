package com.rbkmoney.walker.utils;

import com.bazaarvoice.jolt.JsonUtilImpl;
import com.bazaarvoice.jolt.JsonUtils;
import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.payment_processing.PartyModification;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.geck.serializer.kit.object.ObjectHandler;
import com.rbkmoney.geck.serializer.kit.object.ObjectProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import org.apache.thrift.TBase;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @since 23.03.17
 **/
public class ThriftObjectsConvertor {

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

}
