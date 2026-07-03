package com.pm.notificationservice.kafka;

import com.pm.notificationservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.text.NumberFormat;

@Component
public class KafkaConsumer {
    private final static Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public KafkaConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "email-alerts", groupId = "notification-service-group")
    public void consume(String message) {
        try{
            EmailAlertEvent emailAlert = objectMapper.readValue(message, EmailAlertEvent.class);
            log.info("Received email alert event: {}", emailAlert);

            String subject = "Alert: " + emailAlert.getSymbol() +" has hit your target";
            String body = "Hello,\n\n" +
                    "The cryptocurrency " + emailAlert.getSymbol() + " has " +
                    (emailAlert.getCondition().equals("RISES_ABOVE") ? "risen above" : "dropped below") +
                    " your target price of $" + emailAlert.getTargetPrice() + ".\n" +
                    "Current price: $" + NumberFormat.getCurrencyInstance().format(emailAlert.getCurrentPrice()) + "\n\n" +
                    "Best regards,\n" +
                    "CryptoPulse Service";

            emailService.sendMail(emailAlert.getEmail(), subject, body);
            log.info("Email sent to {} for {}", emailAlert.getEmail(), emailAlert.getSymbol());

        } catch (Exception e) {
            log.error("Failed to process email alert event: {}", message, e);
        }
    }
}