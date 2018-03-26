package com.rbkmoney.utils;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.woody.thrift.impl.http.THPooledClientBuilder;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @since 12.07.17
 **/


@RunWith(SpringRunner.class)
@Ignore
public class HellGateMethodsTest {

    private PartyManagementSrv.Iface partyManagement;
    private EventSinkSrv.Iface eventSink;

    private static String PARTY_MANAGEMENT_SERVICE_URL = "http://hellgate:8022/v1/processing/partymgmt";
    private static String EVENT_SINK_SERVICE_URL = "http://hellgate:8022/v1/processing/eventsink";

    String userId = "1";
    String email = "info@bfsfera.org.ru";
    String partyId = "6954b4d1-f39f-4cc1-8843-eae834e6f849";
    String shopName = "Honey Bunny Winny 1";
    String categoryName = "Sweet Honey";
    String categoryDescription = "Best honey in region. Just try it!";
    String contractId = "contactrId5";
    String shopId = "2";
    String claimId = "4";
    private UserInfo userInfo;

    @Before
    public void setUp() throws Exception {
        partyManagement = new THPooledClientBuilder().withAddress(new URI(PARTY_MANAGEMENT_SERVICE_URL))
                .build(PartyManagementSrv.Iface.class);
        eventSink = new THPooledClientBuilder().withAddress(new URI(EVENT_SINK_SERVICE_URL))
                .build(EventSinkSrv.Iface.class);

        userInfo = new UserInfo(userId, UserType.service_user(new ServiceUser()));
    }

    @Test
    public void eventSink() throws TException {
        List<Event> events = eventSink.getEvents(new EventRange(10));
        events.stream().map(e->{
            System.out.println(e.getPayload().toString());
            return null;
        }).collect(Collectors.toList());
        System.out.println("### Count of events in sink " + events.size());
    }

    @Test
    public void createUser() throws TException {
        PartyParams partyParams = new PartyParams();
        partyParams.setContactInfo(new PartyContactInfo(email));
        partyManagement.create(userInfo, partyId, partyParams);
        System.out.println("#### User created");
    }

    @Test
    public void getParty() throws TException {
        Party party = partyManagement.get(new UserInfo("id", UserType.service_user(new ServiceUser())), partyId);
        Claim claim = partyManagement.getClaim(userInfo, partyId, 3);
        System.out.println(claim.toString());
        System.out.println("### Email: " + party.getContactInfo().getEmail());
    }

    @Test
    public void createContract() throws TException {
        System.out.println(UUID.randomUUID().toString());
        LinkedList<PartyModification> partyModifications = new LinkedList<>();
        partyModifications.add(buildCreateContract());
        partyManagement.createClaim(userInfo, partyId, partyModifications);

//        System.out.println("#### Created shop with ID " + claimResult.getId());
    }

    private PartyModification buildCreateContract() {
        RussianLegalEntity russianLegalEntity = new RussianLegalEntity();
        russianLegalEntity.setActualAddress("Bunny hole");
        russianLegalEntity.setRussianBankAccount(new RussianBankAccount("132", "Bunny bank", "bbbbbuuuuunnny", "bibibiib"));
        russianLegalEntity.setInn("123123");
        russianLegalEntity.setPostAddress("pppaaaa papapa");
        russianLegalEntity.setActualAddress("pipipipip");
        russianLegalEntity.setRegisteredName("True Bunny Inc");
        russianLegalEntity.setRegisteredNumber("243234234");
        russianLegalEntity.setRepresentativePosition("popopo");
        russianLegalEntity.setRepresentativeDocument("dododoc");
        russianLegalEntity.setRepresentativeFullName("nanananna");


        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setRussianLegalEntity(russianLegalEntity);

        Contractor contractor = new Contractor();
        contractor.setLegalEntity(legalEntity);

        ContractParams contractParams = new ContractParams();
        contractParams.setContractor(contractor);

        ContractModification contractModification = new ContractModification();
        contractModification.setCreation(contractParams);

        ContractModificationUnit contractModificationUnit = new ContractModificationUnit();
        contractModificationUnit.setId(contractId);
        contractModificationUnit.setModification(contractModification);

        PartyModification partyModification = new PartyModification();
        partyModification.setContractModification(contractModificationUnit);
        return partyModification;
    }


    @Test
    public void createShop() throws TException {

        System.out.println(UUID.randomUUID().toString());
        LinkedList<PartyModification> partyModifications = new LinkedList<>();

//        partyManagement.createClaim(userInfo,partyId,);

//        System.out.println("#### Created shop with ID " + claimResult.getId());
    }

    private PartyModification buildPartyModifcation() {
        ShopParams shopParams = new ShopParams();
        shopParams.setCategory(new CategoryRef(2));
        shopParams.setContractId("");

        ShopModification shopModification = new ShopModification();
        shopModification.setCreation(shopParams);

        ShopModificationUnit shopModificationUnit = new ShopModificationUnit();
        shopModificationUnit.setId(shopId);
        shopModificationUnit.setModification(shopModification);

        PartyModification partyModification = new PartyModification();
        partyModification.setShopModification(shopModificationUnit);
        return partyModification;
    }

    @Test
    public void updateShop() throws TException {
        // : UserInfo user, 2: PartyID party_id, 3: ShopID id, 4: ShopUpdate update
//        ClaimResult claimResult = partyManagement.updateShop(
//                userInfo,
//                partyId,
//                shopId,
//                buildShopUpdate()
//        );
//        System.out.println("#### Updated shop with ID " + claimResult.getId());
    }

    @Test
    public void revokeClaim() throws TException {
//        partyManagement.revokeClaim(userInfo, partyId, Long.valueOf(claimId), "Revoked from TEST");
    }

    @Test
    public void getShopInfo() throws TException {
        Shop shop = partyManagement.getShop(userInfo, partyId, shopId);
        System.out.println(
                " Shop info  rev.: " + shop.getContractId()
                        + " id: " + shop.getId()
                        + " name: " + shop.getDetails().getName()
                        + " Status " + shop.getSuspension().getFieldValue().toString()
                        + "; Cat name:  " + shop.getCategory().getId()
        );
    }

    @Test
    public void acceptClaim() throws TException, SocketException, NoSuchFieldException, IllegalAccessException {
        List<NetworkInterface> netins = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (NetworkInterface netin : netins) {
            System.out.println(netin + " " + netin.getIndex());
        }
        Field f = NetworkInterface.class.getDeclaredField("defaultIndex");
        f.setAccessible(true);
        System.out.println("defaultIndex = " + f.get(NetworkInterface.class));


        String claimId = "1";
//        partyManagement.acceptClaim(userInfo, partyId, Long.valueOf(claimId));
    }

//    public ShopUpdate buildShopUpdate() {
//        ShopUpdate shopUpdate = new ShopUpdate();
//
//        CategoryObject categoryObject = new CategoryObject();
//        categoryObject.setRef(new CategoryRef(1));
//        categoryObject.setData(new Category("TestCategory_UPDATED", "no descrition_UPDATED"));
//        shopUpdate.setCategory(new CategoryRef(1));
//        return shopUpdate;
//    }
//
//    public ShopParams buildShopParams() {
//        ShopParams shopParams = new ShopParams();
//
//        CategoryObject categoryObject = new CategoryObject();
//        categoryObject.setRef(new CategoryRef(1));
//        categoryObject.setData(new Category(categoryName, categoryDescription));
//        shopParams.setCategory(new CategoryRef(1));
//
//        shopParams.setDetails(new ShopDetails(shopName));
//        return shopParams;
//    }
}
