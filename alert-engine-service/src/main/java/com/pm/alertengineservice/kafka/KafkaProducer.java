package com.pm.alertengineservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String emailAlert) {
        try {
            kafkaTemplate.send("email-alerts", emailAlert);
            log.info("Sent event to Kafka: {}", emailAlert);
        } catch (Exception e) {
            log.error("Failed to send event to Kafka: {}", emailAlert, e);
        }
    }
}
