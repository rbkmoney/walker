package com.rbkmoney.handler;

import com.rbkmoney.AbstractIntegrationTest;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.damsel.payment_processing.Claim;
import com.rbkmoney.damsel.walker.ClaimInfo;
import com.rbkmoney.damsel.walker.ClaimSearchRequest;
import com.rbkmoney.damsel.walker.WalkerSrv;
import com.rbkmoney.geck.serializer.kit.json.JsonHandler;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.handler.PartyEventHandler;
import com.rbkmoney.walker.utils.TimeUtils;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.utils.ActionDiffTest.buildLegalAgreement;
import static org.junit.Assert.assertEquals;


/**
 * @since 30.06.17
 **/
public class EventHandlerTest extends AbstractIntegrationTest {

    @Autowired
    PartyEventHandler partyEventHandler;

    @Autowired
    WalkerSrv.Iface walkerService;

    @Autowired
    ClaimDao claimDao;

    static String PARTY_ID = "test-party-id";
    static long CLAIM_ID = 1L;

    @Before
    public void before() {
        claimDao.getJdbcTemplate().execute("TRUNCATE TABLE walk.claim CONTINUE IDENTITY RESTRICT;");
    }

    @Test
    public void testCreateClaim() throws IOException, TException {
        partyEventHandler.handle(buildClaimCreated());
        ClaimInfo claim = walkerService.getClaim(PARTY_ID, CLAIM_ID);

        assertEquals(PARTY_ID, claim.getPartyId());
        assertEquals(CLAIM_ID, claim.getClaimId());
        assertEquals("pending", claim.getStatus());

        partyEventHandler.handle(buildClaimAccepted());
        ClaimInfo claimAccepted = walkerService.getClaim(PARTY_ID, CLAIM_ID);

        assertEquals(PARTY_ID, claimAccepted.getPartyId());
        assertEquals(CLAIM_ID, claimAccepted.getClaimId());
        assertEquals("accepted", claimAccepted.getStatus());

        ClaimSearchRequest claimSearchRequest = new ClaimSearchRequest();
        claimSearchRequest.setPartyId(PARTY_ID);
        claimSearchRequest.setClaimId(Collections.singleton(Long.valueOf(CLAIM_ID)));
        List<ClaimInfo> claimInfos = walkerService.searchClaims(claimSearchRequest);
        assertEquals(1, claimInfos.size());
        assertEquals(PARTY_ID, claimInfos.get(0).getPartyId());
    }

    @Test
    public void testUpdateClaim() throws IOException, TException {
        partyEventHandler.handle(buildClaimCreated());
        ClaimInfo claim1 = walkerService.getClaim(PARTY_ID, CLAIM_ID);
        int claim1ModSize = claim1.getModifications().getModificationsSize();

        partyEventHandler.handle(buildClaimUpdated());
        ClaimInfo claim2 = walkerService.getClaim(PARTY_ID, CLAIM_ID);
        int claim2ModSize = claim2.getModifications().getModificationsSize();

        assertEquals(claim1ModSize + 1, claim2ModSize);

        ClaimSearchRequest claimSearchRequest = new ClaimSearchRequest();
        claimSearchRequest.setPartyId(PARTY_ID);
        claimSearchRequest.setClaimId(Collections.singleton(Long.valueOf(CLAIM_ID)));
        List<ClaimInfo> claimInfos = walkerService.searchClaims(claimSearchRequest);

        assertEquals(1, claimInfos.size());
        assertEquals(PARTY_ID, claimInfos.get(0).getPartyId());
        assertEquals(claim2ModSize, claimInfos.get(0).getModifications().getModifications().size());
    }


    public StockEvent buildClaimCreated() throws IOException {
        Claim emptyCreated = new Claim();
        Claim claim = new MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(emptyCreated, new TBaseHandler<>(Claim.class));
        claim.setStatus(ClaimStatus.pending(new ClaimPending()));
        claim.setId(CLAIM_ID);

        PartyChange emptyPartyChange = new PartyChange();
        PartyChange partyChange = new MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(emptyPartyChange, new TBaseHandler<>(PartyChange.class));
        partyChange.setClaimCreated(claim);

        EventSource eventSource = new EventSource();
        eventSource.setPartyId(PARTY_ID);

        StockEvent emptyStockEvent = new StockEvent();
        StockEvent stockEvent = new MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(emptyStockEvent, new TBaseHandler<>(StockEvent.class));

        stockEvent.getSourceEvent().getProcessingEvent().setSource(eventSource);
        stockEvent.getSourceEvent().getProcessingEvent().getPayload().setPartyChanges(Collections.singletonList(partyChange));

//        printJson(stockEvent);
        return stockEvent;
    }

    public StockEvent buildClaimUpdated() throws IOException {
        ClaimUpdated claimUpdated = new ClaimUpdated();
        claimUpdated.setId(CLAIM_ID);
        claimUpdated.setRevision(1);
        claimUpdated.setUpdatedAt("2016-01-24T10:15:30Z");
        claimUpdated.setChangeset(Collections.singletonList(buildLegalAgreement()));

        PartyChange emptyPartyChange = new PartyChange();
        PartyChange partyChange = new MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(emptyPartyChange, new TBaseHandler<>(PartyChange.class));
        partyChange.setClaimUpdated(claimUpdated);

        EventSource eventSource = new EventSource();
        eventSource.setPartyId(PARTY_ID);

        StockEvent emptyStockEvent = new StockEvent();
        StockEvent stockEvent = new MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(emptyStockEvent, new TBaseHandler<>(StockEvent.class));

        stockEvent.getSourceEvent().getProcessingEvent().setSource(eventSource);
        stockEvent.getSourceEvent().getProcessingEvent().getPayload().setPartyChanges(Collections.singletonList(partyChange));

        printJson(stockEvent);
        return stockEvent;
    }

    public StockEvent buildClaimAccepted() throws IOException {
        ClaimStatusChanged claimStatusChanged = new ClaimStatusChanged();
        claimStatusChanged.setId(CLAIM_ID);
        claimStatusChanged.setStatus(ClaimStatus.accepted(new ClaimAccepted()));
        claimStatusChanged.setRevision(123);
        claimStatusChanged.setChangedAt(TimeUtils.toIsoInstantString(LocalDateTime.now()));

        PartyChange emptyPartyEvent = new PartyChange();
        PartyChange partyChange = new MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(emptyPartyEvent, new TBaseHandler<>(PartyChange.class));
        partyChange.setClaimStatusChanged(claimStatusChanged);

        EventSource eventSource = new EventSource();
        eventSource.setPartyId(PARTY_ID);

        StockEvent emptyStockEvent = new StockEvent();
        MockTBaseProcessor mockTBaseProcessor = new MockTBaseProcessor(MockMode.REQUIRED_ONLY);
        mockTBaseProcessor.addFieldHandler((handler -> handler.value("2016-01-24T10:15:30Z")), "created_at");
        StockEvent stockEvent = mockTBaseProcessor.process(emptyStockEvent, new TBaseHandler<>(StockEvent.class));

        stockEvent.getSourceEvent().getProcessingEvent().setSource(eventSource);
        stockEvent.getSourceEvent().getProcessingEvent().getPayload().setPartyChanges(Collections.singletonList(partyChange));

        printJson(stockEvent);
        return stockEvent;
    }

    private void printJson(StockEvent stockEvent) {
        try {
            System.out.println("Full json : \n " + new TBaseProcessor().process(stockEvent, new JsonHandler()).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
