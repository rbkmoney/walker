package com.rbkmoney.utils;

import com.bazaarvoice.jolt.utils.JoltUtils;
import com.rbkmoney.damsel.domain.Contractor;
import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.domain.LegalAgreement;
import com.rbkmoney.damsel.domain.LegalEntity;
import com.rbkmoney.damsel.domain.PayoutToolInfo;
import com.rbkmoney.damsel.domain.RussianBankAccount;
import com.rbkmoney.damsel.domain.RussianLegalEntity;
import com.rbkmoney.damsel.payment_processing.ContractModification;
import com.rbkmoney.damsel.payment_processing.ContractModificationUnit;
import com.rbkmoney.damsel.payment_processing.ContractParams;
import com.rbkmoney.damsel.payment_processing.PartyModification;
import com.rbkmoney.damsel.payment_processing.PayoutToolParams;
import com.rbkmoney.damsel.walker.PartyModificationUnit;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.object.ObjectHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.rbkmoney.utils.ThriftToJsonTest.buildDiffObjects;

public class ActionDiffTest {

    public static PartyModification buildWalkerComplexModification() throws IOException {
        RussianBankAccount bankAccount1 = new RussianBankAccount("Аккаунт",
                "Degu Bank Inc", "123123123 post", "12313");

        RussianLegalEntity russianLegalEntity = new RussianLegalEntity();
        russianLegalEntity.setActualAddress("Улица пушкина, Дом колотушкина");
        russianLegalEntity.setInn("АЙНАНЕНАН");
        russianLegalEntity.setPostAddress("Напишимне напиши");
        russianLegalEntity.setRegisteredName("Офшор забугор инкорпарейтед");
        russianLegalEntity.setRegisteredNumber("Какая регистрация?");
        russianLegalEntity.setRepresentativeDocument("Усы лапы и хвост");
        russianLegalEntity.setRepresentativePosition("Миссионерская");
        russianLegalEntity.setRepresentativeFullName("Александра Грей");
        RussianBankAccount bankAccount2 = new RussianBankAccount("Аккаунт2",
                "Not Degu Bank Inc", "333 post", "BIKBIK");
        russianLegalEntity.setRussianBankAccount(bankAccount2);


        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setRussianLegalEntity(russianLegalEntity);

        Contractor contractor = new Contractor();
        contractor.setLegalEntity(legalEntity);

        PayoutToolInfo payoutToolInfo = new PayoutToolInfo();
        payoutToolInfo.setRussianBankAccount(bankAccount2);

        PayoutToolParams payoutToolParams = new PayoutToolParams();
        payoutToolParams.setCurrency(new CurrencyRef("RUB"));
        payoutToolParams.setToolInfo(payoutToolInfo);

        ContractParams contractParams = new ContractParams();
        contractParams.setContractor(contractor);
//        contractParams.setPayoutToolParams(payoutToolParams);


        ContractModification contractModification = new ContractModification();
        contractModification.setCreation(contractParams);

        ContractModificationUnit contractModificationUnit = new ContractModificationUnit();
        contractModificationUnit.setId("1");
        contractModificationUnit.setModification(contractModification);

        PartyModification partyModification = new PartyModification();
        partyModification.setContractModification(contractModificationUnit);
        return partyModification;
    }

    public static PartyModification buildLegalAgreement() {
        LegalAgreement legalAgreement = new LegalAgreement();
        legalAgreement.setLegalAgreementId("006815/07");
        legalAgreement.setSignedAt("2015-06-17T00:00:00Z");

        ContractModification contractModification = new ContractModification();
        contractModification.setLegalAgreementBinding(legalAgreement);

        ContractModificationUnit contractModificationUnit = new ContractModificationUnit();
        contractModificationUnit.setId("2");
        contractModificationUnit.setModification(contractModification);

        PartyModification partyModification = new PartyModification();
        partyModification.setContractModification(contractModificationUnit);
        return partyModification;
    }

    public static Object convertToObjects(PartyModificationUnit partyModificationUnit) throws IOException {
        Object o = JoltUtils.compactJson(new TBaseProcessor().process(partyModificationUnit, new ObjectHandler()));
        return o;
    }

    @Test
    public void test() throws IOException {
        // ПРОЩЕ показывать две разные
        PartyModificationUnit partyModificationUnit1 = new PartyModificationUnit();
        List<PartyModification> partyModificationList = Collections.singletonList(buildWalkerComplexModification());
        partyModificationUnit1.setModifications(partyModificationList);

        LinkedList<PartyModification> partyModificationList2 = new LinkedList<>();
        PartyModification partyModification2 = buildWalkerComplexModification();
        partyModification2.getContractModification().setId("12313");
        partyModificationList2.add(partyModification2);
        partyModificationList2.add(buildRandomModification());

        PartyModificationUnit partyModificationUnit2 = new PartyModificationUnit();
        partyModificationUnit2.setModifications(partyModificationList2);

        Object o1 = convertToObjects(partyModificationUnit1);
        Object o2 = convertToObjects(partyModificationUnit2);
        buildDiffObjects(o1, o2);
    }

    public PartyModification buildRandomModification() throws IOException {
        PartyModification process = new MockTBaseProcessor(MockMode.ALL, 15, 1)
                .process(new PartyModification(), new TBaseHandler<>(PartyModification.class));
        return process;
    }

}
