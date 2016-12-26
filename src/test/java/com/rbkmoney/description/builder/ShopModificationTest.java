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
public class ShopModificationTest {

    @Autowired
    DescriptionBuilder descriptionBuilder;

    @Test
    public void testUpdate() {
        String description = descriptionBuilder.buildDescription(buildUpdateClaim());
        System.out.println(" : \n" + description);
        assertEquals(true, description.contains("* Категория : 1"));
    }

    private Claim buildUpdateClaim() {
        ShopLocation shopLocation = new ShopLocation();
        shopLocation.setUrl("http://misvoihneboriv228.piu");

        ShopDetails shopDetails = new ShopDetails();
        shopDetails.setDescription("Описание магазина");
        shopDetails.setName("Драг стор 228");
        shopDetails.setLocation(shopLocation);

        ShopUpdate shopUpdate = new ShopUpdate();
        shopUpdate.setDetails(shopDetails);
        shopUpdate.setCategory(new CategoryRef(1));

        ShopModification shopModification = new ShopModification();
        shopModification.setUpdate(shopUpdate);

        return buildClaim(shopModification);
    }

    @Test
    public void testAccountCreated() {
        String description = descriptionBuilder.buildDescription(buildAccountClaim());
        System.out.println(" ShopModification Accounts_created: \n" + description);
        assertEquals(true, description.contains("Гарантийный счет: 456"));
    }

    private Claim buildAccountClaim() {
        ShopAccountSet shopAccountSet = new ShopAccountSet();
        shopAccountSet.setCurrency(new CurrencyRef("BTC"));
        shopAccountSet.setGeneral(123);

        shopAccountSet.setGuarantee(456);
        ShopAccountSetCreated shopAccountSetCreated = new ShopAccountSetCreated(shopAccountSet);

        ShopModification shopModification = new ShopModification();
        shopModification.setAccountsCreated(shopAccountSetCreated);

        return buildClaim(shopModification);
    }

    @Test
    public void testBlocked() {
        String description = descriptionBuilder.buildDescription(buildBlocked());
        System.out.println(" ShopModification Accounts_created: \n" + description);
        assertEquals(true, description.contains(" * Заблокирован магазин по причине : За разжигание"));
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
        assertEquals(true, description.contains("* Разблокирован магазин по причине : Потрачено"));
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
        assertEquals(true, description.contains("* Приастановленный магазин активирован"));
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

    private Claim buildClaim(ShopModification shopModification) {
        ShopModificationUnit shopModificationUnit = new ShopModificationUnit();
        shopModificationUnit.setModification(shopModification);

        PartyModification partyModification = new PartyModification();
        partyModification.setShopModification(shopModificationUnit);

        ArrayList<PartyModification> partyModificationChangeSet = new ArrayList<>();
        partyModificationChangeSet.add(partyModification);
        Claim claim = new Claim();
        claim.setId("claim_id").setChangeset(partyModificationChangeSet);
        return claim;
    }
}
