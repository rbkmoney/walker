package com.rbkmoney;

import com.bazaarvoice.jolt.Diffy;
import com.bazaarvoice.jolt.JsonUtilImpl;
import com.bazaarvoice.jolt.JsonUtils;
import com.bazaarvoice.jolt.utils.JoltUtils;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.geck.serializer.kit.object.ObjectHandler;
import com.rbkmoney.geck.serializer.kit.object.ObjectProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import static com.rbkmoney.walker.utils.ThriftConvertor.convertToPartyModificationUnit;
import static com.rbkmoney.walker.utils.ThriftConvertor.convertToWalkerModification;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @since 14.03.17
 **/
public class ThriftToJsonTest {


    @Test
    public void thriftToJsonComplex() throws IOException {
        Object jobjects = JoltUtils.compactJson(new TBaseProcessor().process(buildComplexModification(), new ObjectHandler()));
        String json = JsonUtils.toJsonString(jobjects);

        Object jsonObj = new JsonUtilImpl().jsonToObject(json);
        PartyModification modification = new ObjectProcessor().process(jsonObj, new TBaseHandler<>(PartyModification.class));

        Object o = JoltUtils.compactJson(new TBaseProcessor().process(modification, new ObjectHandler()));
        String json2 = JsonUtils.toJsonString(o);

        System.out.println(json);
        assertEquals(json, json2);
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
    public void testModificationConverter() throws IOException {
        PartyModification modification = buildComplexModification();

        Object hgo = JoltUtils.compactJson(new TBaseProcessor().process(modification, new ObjectHandler()));
        String jsonHG = JsonUtils.toJsonString(hgo);
        com.rbkmoney.damsel.walker.PartyModification partyModification = convertToWalkerModification(modification);
        Object wo = JoltUtils.compactJson(new TBaseProcessor().process(partyModification, new ObjectHandler()));
        String jsonWALK = JsonUtils.toJsonString(wo);
        System.out.println(jsonHG);
        assertEquals(jsonHG, jsonWALK);
    }

    @Test
    public void test() throws IOException {
        Claim claim = new Claim();
        claim.setRevision(1);
        claim.setId(23);
        claim.setStatus(ClaimStatus.accepted(new ClaimAccepted("123")));
        claim.setChangeset(Arrays.asList(buildComplexModification()));

        PartyEvent partyEvent = new PartyEvent();
        partyEvent.setClaimCreated(claim);


        Object jobjects = JoltUtils.compactJson(new TBaseProcessor().process(partyEvent, new ObjectHandler()));
        String json = JsonUtils.toJsonString(jobjects);

        Object jsonObj = new JsonUtilImpl().jsonToObject(json);
        PartyEvent modification = new ObjectProcessor().process(jsonObj, new TBaseHandler<>(PartyEvent.class));

        Object o = JoltUtils.compactJson(new TBaseProcessor().process(modification, new ObjectHandler()));
        String json2 = JsonUtils.toJsonString(o);

        System.out.println(json);
        System.out.println(json2);
    }

    public static String convertToJson(PartyModification partyModification) throws IOException {
        Object o = JoltUtils.compactJson(new TBaseProcessor().process(partyModification, new ObjectHandler()));
        return JsonUtils.toJsonString(o);
    }

    public static Object convertToObjects(PartyModification partyModification) throws IOException {
        Object o = JoltUtils.compactJson(new TBaseProcessor().process(partyModification, new ObjectHandler()));
        return o;
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

        Entity entity = new Entity();
        entity.setRussianLegalEntity(russianLegalEntity);

        Contractor contractor = new Contractor();
        contractor.setBankAccount(bankAccount1);
        contractor.setEntity(entity);

        PayoutToolInfo payoutToolInfo = new PayoutToolInfo();
        payoutToolInfo.setBankAccount(bankAccount2);

        PayoutToolParams payoutToolParams = new PayoutToolParams();
        payoutToolParams.setCurrency(new CurrencyRef("RUB"));
        payoutToolParams.setToolInfo(payoutToolInfo);

        ContractParams contractParams = new ContractParams();
        contractParams.setContractor(contractor);
        contractParams.setPayoutToolParams(payoutToolParams);


        ContractModification contractModification = new ContractModification();
        contractModification.setCreation(contractParams);

        ContractModificationUnit contractModificationUnit = new ContractModificationUnit();
        contractModificationUnit.setModification(contractModification);
        contractModificationUnit.setId("123");

        PartyModification partyModification = new PartyModification();
        partyModification.setContractModification(contractModificationUnit);

        return partyModification;
    }

    @Test
    public void testJsnPth() throws IOException {
        PartyModification modification = buildComplexModification();
        PartyModification modification2 = buildComplexModification();
        modification2.getContractModification().setId("ggggg");
        Object before = convertToObjects(modification);
        Object after = convertToObjects(modification2);

//        System.out.println(diff);
    }

    public static void buildDiffObjects(Object before, Object after) {
        System.out.println("----------------------------------");
        Diffy.Result diffResult = new Diffy().diff(before, after);
        String s = JsonUtils.toPrettyJsonString(diffResult.expected);
        String s1 = JsonUtils.toPrettyJsonString(diffResult.actual);
        System.out.println("FROM : " + s);
        System.out.println("TO : " + s1);

    }

    @Test
    public void testJsonsd(){}

}



