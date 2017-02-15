package com.rbkmoney.description.builder;

import com.rbkmoney.config.TestDescriptionBuilderConfig;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.walker.service.DescriptionBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * @since 26.12.16
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDescriptionBuilderConfig.class)
public class ContractBuilderTest {

    @Autowired
    DescriptionBuilder descriptionBuilder;

    @Test
    public void testContractCreation() {
        String description = descriptionBuilder.buildDescription(buildContractCreationClaim());
        System.out.println(" ContractCreation : \n" + description);
        assertEquals(true, description.contains("* Расчетный счет: Аккаунт"));
    }

    private Claim buildContractCreationClaim() {
        BankAccount bankAccount = new BankAccount("Аккаунт", "Degu Bank Inc", "123123123 post", "12313");

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
        contractor.setBankAccount(bankAccount);
        contractor.setEntity(entity);

        PayoutToolInfo payoutToolInfo = new PayoutToolInfo();
        payoutToolInfo.setBankAccount(bankAccount);

        PayoutTool payoutTool = new PayoutTool();
        payoutTool.setCurrency(new CurrencyRef("RUB"));
        payoutTool.setId(1);
        payoutTool.setPayoutToolInfo(payoutToolInfo);

        Contract contract = new Contract();
        contract.setId(1);
        contract.setValidSince("2017-02-13T18:24:02.346830Z");
        contract.setValidUntil("2017-05-14T18:24:02.346830Z");

        contract.setContractor(contractor);
        contract.setStatus(ContractStatus.active(new ContractActive()));
        contract.setPayoutTools(Collections.singletonList(payoutTool));

        PartyModification partyModification = new PartyModification();
        partyModification.setContractCreation(contract);

        ArrayList<PartyModification> partyModificationChangeSet = new ArrayList<>();
        partyModificationChangeSet.add(partyModification);
        Claim claim = new Claim();
        claim.setId(1000001).setChangeset(partyModificationChangeSet);
        return claim;
    }

    @Test
    public void testModificationTermination() {
        String description = descriptionBuilder.buildDescription(buildContractTerminationClaim());
        System.out.println(" ContractCreation : \n" + description);
        assertEquals(true, description.contains("* Дата расторжения:  01.01.2038"));
    }

    private Claim buildContractTerminationClaim() {
        ContractTermination contractTermination = new ContractTermination();
        contractTermination.setReason("all humans are dead");
        contractTermination.setTerminatedAt("01.01.2038");

        ContractModification contractModification = new ContractModification();
        contractModification.setTermination(contractTermination);

        ContractModificationUnit contractModificationUnit = new ContractModificationUnit();
        contractModificationUnit.setModification(contractModification);
        contractModificationUnit.setId(1);

        PartyModification partyModification = new PartyModification();
        partyModification.setContractModification(contractModificationUnit);

        ArrayList<PartyModification> partyModificationChangeSet = new ArrayList<>();
        partyModificationChangeSet.add(partyModification);
        Claim claim = new Claim();
        claim.setId(1000001).setChangeset(partyModificationChangeSet);
        return claim;
    }

    @Test
    public void testAdjustmentModification() {
        String description = descriptionBuilder.buildDescription(buildContractAdjustmentCreation());
        System.out.println(" ContractModificationAdjustment : \n" + description);
        assertEquals(true, description.contains("* Заключен: 01.02.2034"));
    }

    private Claim buildContractAdjustmentCreation() {
        ContractAdjustment contractAdjustment = new ContractAdjustment();
        contractAdjustment.setId(2);
        contractAdjustment.setValidUntil("01.02.2034");
        contractAdjustment.setTerms(new TermSetHierarchyRef(23));

        ContractModification contractModification = new ContractModification();
        contractModification.setAdjustmentCreation(contractAdjustment);


        ContractModificationUnit contractModificationUnit = new ContractModificationUnit();
        contractModificationUnit.setModification(contractModification);
        contractModificationUnit.setId(1);

        PartyModification partyModification = new PartyModification();
        partyModification.setContractModification(contractModificationUnit);

        ArrayList<PartyModification> partyModificationChangeSet = new ArrayList<>();
        partyModificationChangeSet.add(partyModification);
        Claim claim = new Claim();
        claim.setId(1000001).setChangeset(partyModificationChangeSet);
        return claim;
    }


}
