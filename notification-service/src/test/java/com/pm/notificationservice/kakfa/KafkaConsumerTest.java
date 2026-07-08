package com.pm.notificationservice.kakfa;

import com.pm.notificationservice.kafka.EmailAlertEvent;
import com.pm.notificationservice.kafka.KafkaConsumer;
import com.pm.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @Test
    void alertAboveEmail() {
        String message = "Hello";
        EmailAlertEvent event = new EmailAlertEvent();
        event.setEmail("test@email.com");
        event.setSymbol("SOL");
        event.setCondition("RISES_ABOVE");
        event.setTargetPrice(200.00);
        event.setCurrentPrice(210.00);

        when(objectMapper.readValue(message, EmailAlertEvent.class)).thenReturn(event);

        kafkaConsumer.consume(message);

        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendMail(
               eq("test@email.com"),
                subjectCaptor.capture(),
                bodyCaptor.capture()
        );

        assertTrue(subjectCaptor.getValue().contains("SOL"));
        assertTrue(bodyCaptor.getValue().contains("risen above"));
        assertTrue(bodyCaptor.getValue().contains("200.0"));

    }

    @Test
    void alertBelowEmail() {
        String message = "Hello";
        EmailAlertEvent event = new EmailAlertEvent();
        event.setEmail("test@email.com");
        event.setSymbol("SOL");
        event.setCondition("DROPS_BELOW");
        event.setTargetPrice(100.00);
        event.setCurrentPrice(50.00);

        when(objectMapper.readValue(message, EmailAlertEvent.class)).thenReturn(event);

        kafkaConsumer.consume(message);

        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendMail(
                eq("test@email.com"),
                subjectCaptor.capture(),
                bodyCaptor.capture()
        );

        assertTrue(subjectCaptor.getValue().contains("SOL"));
        assertTrue(bodyCaptor.getValue().contains("dropped below"));
        assertTrue(bodyCaptor.getValue().contains("100.0"));

    }

    @Test
    void shouldNotSendEmail() {
        String message = "INVALID";

        when(objectMapper.readValue(message, EmailAlertEvent.class)).thenThrow(new RuntimeException("Invalid message"));

        kafkaConsumer.consume(message);

        verify(emailService, never()).sendMail(anyString(), anyString(), anyString());
    }
}
