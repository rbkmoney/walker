package com.rbkmoney.kafka;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.walker.service.PartyManagementEventService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@Slf4j
public class PartyManagementKafkaListenerTest extends AbstractKafkaTest {

    @Value("${kafka.topics.party-management.id}")
    public String topic;

    @MockBean
    private PartyManagementEventService partyManagementService;

    private static MachineEvent createMessage() {
        MachineEvent message = new MachineEvent();
        var data = new com.rbkmoney.machinegun.msgpack.Value();
        data.setBin(new byte[0]);
        message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        message.setEventId(1L);
        message.setSourceNs("sad");
        message.setSourceId("sda");
        message.setData(data);
        return message;
    }

    @Test
    public void listenTopic() {
        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(createMessage());

        writeToTopic(topic, sinkEvent);

        verify(partyManagementService, timeout(DEFAULT_KAFKA_SYNC).times(1))
                .handleEvents(anyList());
    }

    @Test
    public void retryEventTest() {
        final AtomicBoolean firstIteration = new AtomicBoolean(true);
        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(createMessage());
        doAnswer(invocation -> {
            if (firstIteration.get()) {
                firstIteration.set(false);
                throw new RuntimeException();
            } else {
                return null;
            }
        }).when(partyManagementService).handleEvents(any());

        writeToTopic(topic, sinkEvent);

        verify(partyManagementService, timeout(20000L).times(2))
                .handleEvents(anyList());
    }

}
