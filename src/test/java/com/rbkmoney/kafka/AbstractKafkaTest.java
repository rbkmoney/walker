package com.rbkmoney.kafka;

import com.rbkmoney.easyway.AbstractTestUtils;
import com.rbkmoney.extension.KafkaContainerExtension;
import com.rbkmoney.extension.PostgresContainerExtension;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.walker.WalkerApplication;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Properties;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@ExtendWith({
        KafkaContainerExtension.class,
        PostgresContainerExtension.class
})
@SpringBootTest(classes = WalkerApplication.class, webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractKafkaTest extends AbstractTestUtils {

    public static final long DEFAULT_KAFKA_SYNC = 5000L;

    @DynamicPropertySource
    static void connectionConfigs(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PostgresContainerExtension.POSTGRES::getJdbcUrl);
        registry.add("flyway.url", PostgresContainerExtension.POSTGRES::getJdbcUrl);
        registry.add("kafka.topics.party-management.enabled", () -> "true");
        registry.add("kafka.bootstrap-servers", KafkaContainerExtension.KAFKA::getBootstrapServers);
    }

    protected void writeToTopic(String topic, SinkEvent sinkEvent) {
        Producer<String, SinkEvent> producer = createProducer();
        ProducerRecord<String, SinkEvent> producerRecord = new ProducerRecord<>(topic, null, sinkEvent);
        try {
            producer.send(producerRecord).get();
        } catch (Exception e) {
            log.error("KafkaAbstractTest initialize e: ", e);
        }
        producer.close();
    }

    private Producer<String, SinkEvent> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaContainerExtension.KAFKA.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "client_id");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new ThriftSerializer<SinkEvent>().getClass());
        return new KafkaProducer<>(props);
    }

}
