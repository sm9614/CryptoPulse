package com.pm.alertengineservice.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerTest {

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    KafkaProducer kafkaProducer;

    @Test
    void shouldSendEvent() {
        String message = "{\"email\":\"test@test.com\"}";
        kafkaProducer.sendEvent(message);
        verify(kafkaTemplate).send("email-alerts", message);
    }
}
