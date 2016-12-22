package com.rbkmoney;

import com.rbkmoney.config.TestDescriptionBuilderConfig;
import com.rbkmoney.damsel.domain.Shop;
import com.rbkmoney.damsel.domain.ShopDetails;
import com.rbkmoney.damsel.domain.ShopLocation;
import com.rbkmoney.damsel.payment_processing.Claim;
import com.rbkmoney.damsel.payment_processing.PartyModification;
import com.rbkmoney.walker.handler.DescriptionBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

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
        String s = descriptionBuilder.buildDescription(buildClaim());
        System.out.println(" ShopCreation: \n " + s);
    }

    private Claim buildClaim() {
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
