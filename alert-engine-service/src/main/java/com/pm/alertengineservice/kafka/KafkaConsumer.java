package com.pm.alertengineservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.alertengineservice.model.Alert;
import com.pm.alertengineservice.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;

@Component
public class KafkaConsumer {
    private final static Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    private final ObjectMapper objectMapper;
    private final KafkaProducer kafkaProducer;
    private final AlertRepository alertRepository;

    public KafkaConsumer(ObjectMapper objectMapper,
                         KafkaProducer kafkaProducer,
                         AlertRepository alertRepository) {

        this.objectMapper = objectMapper;
        this.kafkaProducer = kafkaProducer;
        this.alertRepository = alertRepository;
    }

    @KafkaListener(topics = "crypto-prices", groupId = "alert-engine-group")
    public void consume(String message) {
        try {
            PriceEvent priceEvent = objectMapper.readValue(message, PriceEvent.class);
            String symbol = priceEvent.getSymbol();
            double price = priceEvent.getPrice();
            alertRepository.findByStatusAndCoin("ACTIVE", symbol).forEach(alert -> processAlert(alert, symbol, price));

            log.info("Parsed price event: {} - ${}", priceEvent.getSymbol(), priceEvent.getPrice());
        } catch (Exception e) {
            log.error("Failed to consume message from Kafka: {}", message, e);
        }
    }

    private void processAlert(Alert alert, String symbol, double price) {
        boolean triggered =
                (alert.getCondition().equals("RISES_ABOVE") && price >= alert.getTargetPrice()) ||
                (alert.getCondition().equals("DROPS_BELOW") && price <= alert.getTargetPrice());

        if (!triggered) return;

        try {
            EmailAlertEvent emailAlert = new EmailAlertEvent();
            emailAlert.setEmail(alert.getEmail());
            emailAlert.setSymbol(symbol);
            emailAlert.setTargetPrice(alert.getTargetPrice());
            emailAlert.setCondition(alert.getCondition());
            emailAlert.setCurrentPrice(price);
            kafkaProducer.sendEvent(objectMapper.writeValueAsString(emailAlert));
            alert.setStatus("COMPLETED");
            log.info("Alert triggered for {} at ${}", symbol, NumberFormat.getCurrencyInstance().format(price));

        } catch (Exception e) {
            log.error("Failed to create email alert event for alert: {}", alert, e);
        }

    }
}
