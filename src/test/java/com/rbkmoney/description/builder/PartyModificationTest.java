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

import static org.junit.Assert.assertEquals;

/**
 * @since 26.12.16
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDescriptionBuilderConfig.class)
public class PartyModificationTest {

    @Autowired
    DescriptionBuilder descriptionBuilder;

    @Test
    public void testBlocked() {
        String description = descriptionBuilder.buildDescription(buildBlocked());
        System.out.println(" ShopModification Accounts_created: \n" + description);
        assertEquals(true, description.contains("* Заблокирован магазин по причине: За разжигание"));
    }

    private Claim buildBlocked() {
        Blocked blocked = new Blocked("За разжигание");
        Blocking blocking = new Blocking();
        blocking.setBlocked(blocked);
        ShopModification shopModification = new ShopModification();
        shopModification.setBlocking(blocking);
        return buildClaim(shopModification);
    }

    @Test
    public void testUnblock() {
        String description = descriptionBuilder.buildDescription(buildUnblock());
        System.out.println(" ShopModification Accounts_created: \n" + description);
        assertEquals(true, description.contains("* Разблокирован магазин по причине: Потрачено"));
    }

    private Claim buildUnblock() {
        Unblocked unblocked = new Unblocked("Потрачено");
        Blocking blocking = new Blocking();
        blocking.setUnblocked(unblocked);
        ShopModification shopModification = new ShopModification();
        shopModification.setBlocking(blocking);
        return buildClaim(shopModification);
    }

    @Test
    public void testSuspension() {
        String description = descriptionBuilder.buildDescription(buildShopModificationSuspensionActive());
        System.out.println(" ShopModification Accounts_created: \n" + description);
        assertEquals(true, description.contains("* Приостановленный магазин активирован"));
    }

    private Claim buildShopModificationSuspensionActive() {
        Suspension suspension = new Suspension();
        suspension.setActive(new Active());
        ShopModification shopModification = new ShopModification();
        shopModification.setSuspension(suspension);
        return buildClaim(shopModification);
    }

    @Test
    public void testActivationSuspend() {
        String description = descriptionBuilder.buildDescription(buildSuspensionSuspend());
        System.out.println(" ShopModification Accounts_created: \n" + description);
        assertEquals(true, description.contains("* Приастановлен магазин"));
    }

    private Claim buildSuspensionSuspend() {
        Suspension suspension = new Suspension();
        suspension.setSuspended(new Suspended());
        ShopModification shopModification = new ShopModification();
        shopModification.setSuspension(suspension);
        return buildClaim(shopModification);
    }

    @Test
    public void testAccountCreated() {
        String description = descriptionBuilder.buildDescription(buildAccountClaim());
        System.out.println(" ShopModification Accounts_created: \n" + description);
        assertEquals(true, description.contains("Гарантийный счет: 111"));
    }

    private Claim buildAccountClaim() {
        ShopAccount shopAccount = new ShopAccount();
        shopAccount.setCurrency(new CurrencyRef("BTC"));
        shopAccount.setGuarantee(111);
        shopAccount.setSettlement(222);

        ShopAccountCreated shopAccountCreated = new ShopAccountCreated();
        shopAccountCreated.setAccount(shopAccount);

        ShopModification shopModification = new ShopModification();
        shopModification.setAccountCreated(shopAccountCreated);
        return buildClaim(shopModification);
    }


    public static Claim buildClaim(ShopModification shopModification) {
        ShopModificationUnit shopModificationUnit = new ShopModificationUnit();
        shopModificationUnit.setModification(shopModification);

        PartyModification partyModification = new PartyModification();
        partyModification.setShopModification(shopModificationUnit);

        ArrayList<PartyModification> partyModificationChangeSet = new ArrayList<>();
        partyModificationChangeSet.add(partyModification);
        Claim claim = new Claim();
        claim.setId(1000001).setChangeset(partyModificationChangeSet);
        return claim;
    }
}
