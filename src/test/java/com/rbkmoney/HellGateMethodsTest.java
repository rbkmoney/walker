package com.rbkmoney;

import com.rbkmoney.damsel.domain.Category;
import com.rbkmoney.damsel.domain.CategoryObject;
import com.rbkmoney.damsel.domain.CategoryRef;
import com.rbkmoney.damsel.domain.ShopDetails;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.List;


@RunWith(SpringRunner.class)
@Ignore
public class HellGateMethodsTest {

    private PartyManagementSrv.Iface partyManagement;
    private EventSinkSrv.Iface eventSink;

    private static String PARTY_MANAGEMENT_SERVICE_URL = "http://localhost:8022/v1/processing/partymgmt";
    private static String EVENT_SINK_SERVICE_URL = "http://localhost:8022/v1/processing/eventsink";

    String userId = "Vinni Puh";
    String partyId = "Medovarnya LTD";
    String shopName = "Honey Bunny Winny 1";
    String categoryName = "Sweet Honey";
    String categoryDescription = "Best honey in region. Just try it!";
    String shopId = "2";
    String claimId = "3";

    @Before
    public void setUp() throws Exception {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withHttpClient(HttpClientBuilder.create().build())
                .withAddress(new URI(PARTY_MANAGEMENT_SERVICE_URL));
        partyManagement = clientBuilder.build(PartyManagementSrv.Iface.class);

        THClientBuilder cb = new THClientBuilder()
                .withHttpClient(HttpClientBuilder.create().build())
                .withAddress(new URI(EVENT_SINK_SERVICE_URL));
        eventSink = cb.build(EventSinkSrv.Iface.class);
    }

    @Test
    public void eventSink() throws TException {
        List<Event> events = eventSink.getEvents(new EventRange(10));
        System.out.println("### Count of events in sink " + events.size());
    }

    @Test
    public void createUser() throws TException {
        partyManagement.create(new UserInfo(userId), partyId);
        System.out.println("#### User created");
    }

    @Test
    public void createShop() throws TException {
        ClaimResult shop = partyManagement.createShop(
                new UserInfo(userId),
                partyId,
                buildShopParams()
        );
        System.out.println("#### Created shop with ID " + shop.getId());
    }

    @Test
    public void updateShop() throws TException {
        ClaimResult shop = partyManagement.updateShop(
                new UserInfo(userId),
                partyId,
                shopId,
                buildShopUpdate()
        );
        System.out.println("#### Updated shop with ID " + shop.getId());
    }

    @Test
    public void revokeClaim() throws TException {
        partyManagement.revokeClaim(new UserInfo(userId), partyId, claimId, "Revoked from TEST");
    }

    @Test
    public void getShopInfo() throws TException {
        ShopState shop = partyManagement.getShop(new UserInfo(userId), partyId, shopId);
        System.out.println(
                " Shop info  rev.: " + shop.getRevision()
                        + " id: " + shop.getShop().getId()
                        + " name: " + shop.getShop().getDetails().getName()
                        + " Status " + shop.getShop().getSuspension().getFieldValue().toString()
                        + "; Cat name:  " + shop.getShop().getCategory().getId()
        );
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
        shopUpdate.setCategory(new CategoryRef(1));
        return shopUpdate;
    }

    public ShopParams buildShopParams() {
        ShopParams shopParams = new ShopParams();

        CategoryObject categoryObject = new CategoryObject();
        categoryObject.setRef(new CategoryRef(1));
        categoryObject.setData(new Category(categoryName, categoryDescription));
        shopParams.setCategory(new CategoryRef(1));

        shopParams.setDetails(new ShopDetails(shopName));
        return shopParams;
    }
}
