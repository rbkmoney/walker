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





}
