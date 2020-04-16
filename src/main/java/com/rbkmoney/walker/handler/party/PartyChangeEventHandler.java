package com.rbkmoney.walker.handler.party;

import com.rbkmoney.damsel.payment_processing.PartyChange;

import java.io.IOException;

public interface PartyChangeEventHandler {

    void handleEvent(PartyChange partyChange, long eventId, String partyId) throws IOException;

}
