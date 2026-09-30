package com.example.testbanking.transactionservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    private static final long INTERVAL = 1000L;
    private static final long MAX_ATTEMPTS = 3L;

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> template) {
        final FixedBackOff backOff = new FixedBackOff(INTERVAL, MAX_ATTEMPTS);
        final DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);

        return new DefaultErrorHandler(recoverer, backOff);
    }
}
