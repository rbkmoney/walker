package com.rbkmoney;

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
 * @since 22.12.16
 **/

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDescriptionBuilderConfig.class)
public class DescriptionBuilderTest {
    @Autowired
    DescriptionBuilder descriptionBuilder;

    @Test
    public void testShopCreation() {
        String description = descriptionBuilder.buildDescription(buildShopCreationClaim());
        System.out.println(" ShopCreation: \n " + description);
        assertEquals(true, description.contains("* Местоположение: http://misvoihneboriv228.piu"));
    }

    private Claim buildShopCreationClaim() {
        ShopLocation shopLocation = new ShopLocation();
        shopLocation.setUrl("http://misvoihneboriv228.piu");

        ShopDetails shopDetails = new ShopDetails();
        shopDetails.setDescription("Описание магазина");
        shopDetails.setName("Драг стор 228");
        shopDetails.setLocation(shopLocation);

        Shop shop = new Shop();
        shop.setId(0).setContractId(10).setDetails(shopDetails);

        PartyModification partyModification = new PartyModification();
        partyModification.setShopCreation(shop);

        ArrayList<PartyModification> partyModificationChangeSet = new ArrayList<>();
        partyModificationChangeSet.add(partyModification);
        Claim claim = new Claim();
        claim.setId("claim_id").setChangeset(partyModificationChangeSet);
        return claim;
    }

    @Test
    public void testShopModificationUpdate() {
        String description = descriptionBuilder.buildDescription(buildShopModificationUpdateClaim());
        System.out.println(" ShopModification: \n" + description);
        assertEquals(true, description.contains("* Категория : 1"));
    }

    private Claim buildShopModificationUpdateClaim() {
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

    @Test
    public void testShopModificationAccountCreated() {
        String description = descriptionBuilder.buildDescription(buildShopModificationAccountClaim());
        System.out.println(" ShopModification Accounts_created: \n" + description);
        assertEquals(true, description.contains("Гарантийный счет: 456"));
    }

    private Claim buildShopModificationAccountClaim() {
        ShopAccountSet shopAccountSet = new ShopAccountSet();
        shopAccountSet.setCurrency(new CurrencyRef("BTC"));
        shopAccountSet.setGeneral(123);

        shopAccountSet.setGuarantee(456);
        ShopAccountSetCreated shopAccountSetCreated = new ShopAccountSetCreated(shopAccountSet);

        ShopModification shopModification = new ShopModification();
        shopModification.setAccountsCreated(shopAccountSetCreated);

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

    @Test
    public void testShopModificationBlocked() {
        String description = descriptionBuilder.buildDescription(buildShopModificationBlocked());
        System.out.println(" ShopModification Accounts_created: \n" + description);
        assertEquals(true, description.contains(" * Заблокирован магазин по причине : За разжигание"));
    }

    private Claim buildShopModificationBlocked() {
        Blocked blocked = new Blocked("За разжигание");
        Blocking blocking = new Blocking();
        blocking.setBlocked(blocked);

        ShopModification shopModification = new ShopModification();
        shopModification.setBlocking(blocking);

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

    @Test
    public void testShopModificationUnblock() {
        String description = descriptionBuilder.buildDescription(buildShopModificationUnblock());
        System.out.println(" ShopModification Accounts_created: \n" + description);
        assertEquals(true, description.contains("* Разблокирован магазин по причине : Потрачено"));
    }

    private Claim buildShopModificationUnblock() {
        Unblocked unblocked = new Unblocked("Потрачено");
        Blocking blocking = new Blocking();
        blocking.setUnblocked(unblocked);

        ShopModification shopModification = new ShopModification();
        shopModification.setBlocking(blocking);

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
