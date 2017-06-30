package com.rbkmoney.utils;

import com.bazaarvoice.jolt.Diffy;
import com.bazaarvoice.jolt.JsonUtilImpl;
import com.bazaarvoice.jolt.JsonUtils;
import com.bazaarvoice.jolt.utils.JoltUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.geck.serializer.kit.json.JsonHandler;
import com.rbkmoney.geck.serializer.kit.json.JsonProcessor;
import com.rbkmoney.geck.serializer.kit.object.ObjectHandler;
import com.rbkmoney.geck.serializer.kit.object.ObjectProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import static com.rbkmoney.walker.utils.ThriftConvertor.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @since 14.03.17
 **/
public class ThriftToJsonTest {


    @Test
    public void thriftToJsonComplex() throws IOException {
        PartyModification partyModification = buildComplexModification();
        String json1 = convertToJson(partyModification);

        PartyModification partyModification1 = fromJsonPartyModification(json1);
        String json2 = convertToJson(partyModification1);

        assertEquals(json1, json2);
    }

    @Test
    public void thriftMigrationsTest() throws IOException {
        LinkedList<PartyModification> partyModifications = new LinkedList<>();
        partyModifications.add(buildComplexModification());
        partyModifications.add(buildComplexModification());
        PartyModificationUnit partyModificationUnit = convertToPartyModificationUnit(partyModifications);

        assertTrue(partyModificationUnit.getModifications().get(0).isSetContractModification());
    }

    @Test
    public void testEventConvert() throws IOException {
        Claim claim = new Claim();
        claim.setRevision(1);
        claim.setId(23);
        claim.setStatus(ClaimStatus.accepted(new ClaimAccepted()));
        claim.setChangeset(Arrays.asList(buildComplexModification()));
        claim.setCreatedAt("TEST DATE");
        claim.setStatus(ClaimStatus.pending(new ClaimPending()));

        PartyEvent partyEvent = new PartyEvent();
        partyEvent.setClaimCreated(claim);

        String json1 = convertToJson(partyEvent);
        PartyEvent partyEvent1 = fromJsonPartyEvent(json1);

        String json2 = convertToJson(partyEvent1);

        assertEquals(json1,json2);
    }

    public static PartyModification buildComplexModification() {

        BankAccount bankAccount1 = new BankAccount("Аккаунт", "Degu Bank Inc", "123123123 post", "12313");
        BankAccount bankAccount2 = new BankAccount("Аккаунт2", "Not Degu Bank Inc", "333 post", "BIKBIK");

        RussianLegalEntity russianLegalEntity = new RussianLegalEntity();
        russianLegalEntity.setActualAddress("Улица пушкина, Дом колотушкина");
        russianLegalEntity.setInn("АЙНАНЕНАН");
        russianLegalEntity.setPostAddress("Напишимне напиши");
        russianLegalEntity.setRegisteredName("Офшор забугор инкорпарейтед");
        russianLegalEntity.setRegisteredNumber("Какая регистрация?");
        russianLegalEntity.setRepresentativeDocument("Усы лапы и хвост");
        russianLegalEntity.setRepresentativePosition("Миссионерская");
        russianLegalEntity.setRepresentativeFullName("Александра Грей");
        russianLegalEntity.setBankAccount(bankAccount1);

        LegalEntity legalEntity = new LegalEntity();

        legalEntity.setRussianLegalEntity(russianLegalEntity);
        Contractor contractor = new Contractor();
        contractor.setLegalEntity(legalEntity);

        PayoutToolInfo payoutToolInfo = new PayoutToolInfo();
        payoutToolInfo.setBankAccount(bankAccount2);

        PayoutToolParams payoutToolParams = new PayoutToolParams();
        payoutToolParams.setCurrency(new CurrencyRef("RUB"));
        payoutToolParams.setToolInfo(payoutToolInfo);

        ContractParams contractParams = new ContractParams();
        contractParams.setContractor(contractor);

        ContractModification contractModification = new ContractModification();
        contractModification.setCreation(contractParams);

        ContractModificationUnit contractModificationUnit = new ContractModificationUnit();
        contractModificationUnit.setModification(contractModification);
        contractModificationUnit.setId("123");

        PartyModification partyModification = new PartyModification();
        partyModification.setContractModification(contractModificationUnit);

        return partyModification;
    }

    public static void buildDiffObjects(Object before, Object after) {
        System.out.println("----------------------------------");
        Diffy.Result diffResult = new Diffy().diff(before, after);
        String s = JsonUtils.toPrettyJsonString(diffResult.expected);
        String s1 = JsonUtils.toPrettyJsonString(diffResult.actual);
        System.out.println("FROM : " + s);
        System.out.println("TO : " + s1);
    }

}



