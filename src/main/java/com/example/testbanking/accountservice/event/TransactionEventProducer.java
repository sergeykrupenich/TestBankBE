package com.example.testbanking.accountservice.event;

import com.example.testbanking.common.dto.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionEventProducer {

    private static final String TOPIC = "transaction-events";
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public void sendTransactionEvent(TransactionEvent event) {
        final String key = event.getTransactionId().toString();

        log.debug("Sending transaction event to Kafka. Transaction ID: {}", key);

        kafkaTemplate.send(TOPIC, key, event).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent transaction event to Kafka. Transaction ID: {}, Offset: {}",
                        key, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send transaction event to Kafka. Transaction ID: {}", key, ex);
            }
        });
    }
}
