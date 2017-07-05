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


    public StockEvent buildClaimCreated() throws IOException {
        Claim emptyCreated = new Claim();
        Claim claim = new MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(emptyCreated, new TBaseHandler<>(Claim.class));
        claim.setStatus(ClaimStatus.pending(new ClaimPending()));
        claim.setId(CLAIM_ID);

        PartyEvent emptyPartyEvent = new PartyEvent();
        PartyEvent partyEvent = new MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(emptyPartyEvent, new TBaseHandler<>(PartyEvent.class));
        partyEvent.setClaimCreated(claim);

        EventSource eventSource = new EventSource();
        eventSource.setParty(PARTY_ID);

        StockEvent emptyStockEvent = new StockEvent();
        StockEvent stockEvent = new MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(emptyStockEvent, new TBaseHandler<>(StockEvent.class));

        stockEvent.getSourceEvent().getProcessingEvent().setSource(eventSource);
        stockEvent.getSourceEvent().getProcessingEvent().getPayload().setPartyEvent(partyEvent);

//        printJson(stockEvent);
        return stockEvent;
    }

    public StockEvent buildClaimAccepted() throws IOException {
        ClaimStatusChanged claimStatusChanged = new ClaimStatusChanged();
        claimStatusChanged.setId(CLAIM_ID);
        claimStatusChanged.setStatus(ClaimStatus.accepted(new ClaimAccepted()));
        claimStatusChanged.setRevision(123);
        claimStatusChanged.setChangedAt(TimeUtils.toIsoInstantString(LocalDateTime.now()));

        PartyEvent emptyPartyEvent = new PartyEvent();
        PartyEvent partyEvent = new MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(emptyPartyEvent, new TBaseHandler<>(PartyEvent.class));
        partyEvent.setClaimStatusChanged(claimStatusChanged);

        EventSource eventSource = new EventSource();
        eventSource.setParty(PARTY_ID);

        StockEvent emptyStockEvent = new StockEvent();
        StockEvent stockEvent = new MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(emptyStockEvent, new TBaseHandler<>(StockEvent.class));

        stockEvent.getSourceEvent().getProcessingEvent().setSource(eventSource);
        stockEvent.getSourceEvent().getProcessingEvent().getPayload().setPartyEvent(partyEvent);

//        printJson(stockEvent);
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
