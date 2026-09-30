package com.example.testbanking.transactionservice;

import com.example.testbanking.common.dto.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    @KafkaListener(
            topics = "transaction-events",
            groupId = "audit-ledger-group"
    )
    @Transactional
    public void consume(TransactionEvent event) {
        final String eventId = event.getTransactionId().toString();

        // 1. Handling Duplicate Messages
        if (!processedEventIds.add(eventId)) {
            log.warn("[Kafka Consumer] Duplicate message detected! Skipping Transaction ID: {}", eventId);
            return;
        }

        // 2. Perform an additional action (Audit / Ledger)
        log.info("[AUDIT LEDGER] Event processed successfully: TxID={}, Amount={} {}, Source={}, Target={}",
                event.getTransactionId(),
                event.getAmount(),
                event.getCurrency(),
                event.getSourceAccountNumber(),
                event.getTargetAccountNumber()
        );
    }
}
