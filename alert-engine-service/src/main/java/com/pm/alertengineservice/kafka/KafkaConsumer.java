package com.pm.alertengineservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final static Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    private final ObjectMapper objectMapper;

    public KafkaConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "crypto-prices", groupId = "alert-engine-group")
    public void consume(String message) {
        try {
            PriceEvent priceEvent = objectMapper.readValue(message, PriceEvent.class);
            log.info("Parsed price event: {} - ${}", priceEvent.getSymbol(), priceEvent.getPrice());
        } catch (Exception e) {
            log.error("Failed to consume message from Kafka: {}", message, e);
        }
    }
}
