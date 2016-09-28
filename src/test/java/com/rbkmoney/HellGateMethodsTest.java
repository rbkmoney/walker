package com.rbkmoney;

import com.rbkmoney.damsel.domain.Category;
import com.rbkmoney.damsel.domain.CategoryObject;
import com.rbkmoney.damsel.domain.CategoryRef;
import com.rbkmoney.damsel.domain.ShopDetails;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;


@RunWith(SpringRunner.class)
public class HellGateMethodsTest {
    private EventSinkSrv.Iface eventSinkClient;

    private PartyManagementSrv.Iface partyManagement;


    private static String SERVICE_URL = "http://localhost:8022/v1/processing/eventsink";

    private static String PARTY_MANAGEMENT_SERVICE_URL = "http://localhost:8022/v1/processing/partymgmt";

    String userId = "Vinni Puh";
    String partyId = "Medovarnya LTD";
    String shopName = "Honey Bunny";
    String categoryName = "Sweet Honey";
    String categoryDescription = "Best honey in region. Just try it!";


    @Before
    public void setUp() throws Exception {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withHttpClient(HttpClientBuilder.create().build())
                .withAddress(new URI(PARTY_MANAGEMENT_SERVICE_URL));
        partyManagement = clientBuilder.build(PartyManagementSrv.Iface.class);
    }

    @Test
    public void createUser() throws TException {
        partyManagement.create(new UserInfo(userId), partyId);
        System.out.println("User created");
    }

    @Test
    public void createShop() throws TException {
        ClaimResult shop = partyManagement.createShop(
                new UserInfo(userId),
                partyId,
                buildShopParams()
        );
        System.out.println("Created shop with ID " + shop.getId());
    }

    @Test
    public void updateShop() throws TException {
        String shopId = "1";
        ClaimResult shop = partyManagement.updateShop(
                new UserInfo(userId),
                partyId,
                shopId,
                buildShopUpdate()
        );
        System.out.println("Updated shop with ID " + shop.getId());
    }

    @Test
    public void acceptClaim() throws TException {
        String claimId = "1";
        partyManagement.acceptClaim(new UserInfo("MyTestUser"), partyId, claimId);
    }


    public ShopUpdate buildShopUpdate() {
        ShopUpdate shopUpdate = new ShopUpdate();

        CategoryObject categoryObject = new CategoryObject();
        categoryObject.setRef(new CategoryRef(1));
        categoryObject.setData(new Category("TestCategory_UPDATED", "no descrition_UPDATED"));
        shopUpdate.setCategory(categoryObject);
        return shopUpdate;
    }


    public ShopParams buildShopParams() {
        ShopParams shopParams = new ShopParams();

        CategoryObject categoryObject = new CategoryObject();
        categoryObject.setRef(new CategoryRef(1));
        categoryObject.setData(new Category(categoryName, categoryDescription));
        shopParams.setCategory(categoryObject);

        shopParams.setDetails(new ShopDetails(shopName));
        return shopParams;

    }
}
