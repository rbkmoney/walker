package com.rbkmoney.handler;

import com.rbkmoney.AbstractIntegrationTest;
import com.rbkmoney.damsel.payment_processing.Claim;
import com.rbkmoney.damsel.payment_processing.ClaimAccepted;
import com.rbkmoney.damsel.payment_processing.ClaimPending;
import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.payment_processing.ClaimStatusChanged;
import com.rbkmoney.damsel.payment_processing.ClaimUpdated;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyEventData;
import com.rbkmoney.damsel.walker.ClaimInfo;
import com.rbkmoney.damsel.walker.ClaimSearchRequest;
import com.rbkmoney.damsel.walker.WalkerSrv;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.walker.dao.ClaimDao;
import com.rbkmoney.walker.handler.PartyEventHandler;
import com.rbkmoney.walker.utils.TimeUtils;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.rbkmoney.utils.ActionDiffTest.buildLegalAgreement;
import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventHandlerTest extends AbstractIntegrationTest {

    private static final String PARTY_ID = "test-party-id";
    private static final long CLAIM_ID = 1L;
    @Autowired
    private PartyEventHandler partyEventHandler;
    @Autowired
    private WalkerSrv.Iface walkerService;
    @Autowired
    private ClaimDao claimDao;

    private static MachineEvent createTestMachineEvent() {
        MachineEvent event = new MachineEvent();
        event.setEventId(random(Long.class));
        event.setSourceId(PARTY_ID);
        return event;
    }

    private static PartyEventData createTestPartyEventData(PartyChange partyChange) {
        PartyEventData eventData = new PartyEventData();
        eventData.setChanges(Arrays.asList(partyChange));
        return eventData;
    }

    @BeforeEach
    public void before() {
        claimDao.getJdbcTemplate().execute("TRUNCATE TABLE walk.claim CONTINUE IDENTITY RESTRICT;");
    }

    @Test
    public void testCreateClaim() throws IOException, TException {
        partyEventHandler.handle(createTestMachineEvent(), createTestPartyEventData(buildClaimCreated()));
        ClaimInfo claim = walkerService.getClaim(PARTY_ID, CLAIM_ID);

        assertEquals(PARTY_ID, claim.getPartyId());
        assertEquals(CLAIM_ID, claim.getClaimId());
        assertEquals("pending", claim.getStatus());

        partyEventHandler.handle(createTestMachineEvent(), createTestPartyEventData(buildClaimAccepted()));
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
        partyEventHandler.handle(createTestMachineEvent(), createTestPartyEventData(buildClaimCreated()));
        ClaimInfo claim1 = walkerService.getClaim(PARTY_ID, CLAIM_ID);
        int claim1ModSize = claim1.getModifications().getModificationsSize();

        partyEventHandler.handle(createTestMachineEvent(), createTestPartyEventData(buildClaimUpdated()));
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

    public PartyChange buildClaimCreated() throws IOException {
        Claim emptyCreated = new Claim();
        Claim claim = new MockTBaseProcessor(MockMode.REQUIRED_ONLY, 15, 1)
                .process(emptyCreated, new TBaseHandler<>(Claim.class));
        claim.setStatus(ClaimStatus.pending(new ClaimPending()));
        claim.setId(CLAIM_ID);

        PartyChange emptyPartyChange = new PartyChange();
        PartyChange partyChange = new MockTBaseProcessor(MockMode.REQUIRED_ONLY, 15, 1)
                .process(emptyPartyChange, new TBaseHandler<>(PartyChange.class));
        partyChange.setClaimCreated(claim);

        return partyChange;
    }

    public PartyChange buildClaimUpdated() throws IOException {
        ClaimUpdated claimUpdated = new ClaimUpdated();
        claimUpdated.setId(CLAIM_ID);
        claimUpdated.setRevision(1);
        claimUpdated.setUpdatedAt("2016-01-24T10:15:30Z");
        claimUpdated.setChangeset(Collections.singletonList(buildLegalAgreement()));

        PartyChange emptyPartyChange = new PartyChange();
        PartyChange partyChange = new MockTBaseProcessor(MockMode.REQUIRED_ONLY, 15, 1)
                .process(emptyPartyChange, new TBaseHandler<>(PartyChange.class));
        partyChange.setClaimUpdated(claimUpdated);

        return partyChange;
    }

    public PartyChange buildClaimAccepted() throws IOException {
        ClaimStatusChanged claimStatusChanged = new ClaimStatusChanged();
        claimStatusChanged.setId(CLAIM_ID);
        claimStatusChanged.setStatus(ClaimStatus.accepted(new ClaimAccepted()));
        claimStatusChanged.setRevision(123);
        claimStatusChanged.setChangedAt(TimeUtils.toIsoInstantString(LocalDateTime.now()));

        PartyChange emptyPartyEvent = new PartyChange();
        PartyChange partyChange = new MockTBaseProcessor(MockMode.REQUIRED_ONLY, 15, 1)
                .process(emptyPartyEvent, new TBaseHandler<>(PartyChange.class));
        partyChange.setClaimStatusChanged(claimStatusChanged);

        return partyChange;
    }

}
