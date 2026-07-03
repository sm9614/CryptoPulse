package com.pm.alertengineservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.alertengineservice.model.Alert;
import com.pm.alertengineservice.repository.AlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @Test
    void shouldTriggerAlertWhenPriceAboveAlert() throws Exception {
        PriceEvent event = new PriceEvent();
        event.setSymbol("BTC");
        event.setPrice(50000.00);

        Alert alert = new Alert();
        alert.setEmail("Test@email.com");
        alert.setCoin("BTC");
        alert.setStatus("ACTIVE");
        alert.setCondition("RISES_ABOVE");
        alert.setTargetPrice(40000.00);

        when(objectMapper.readValue(anyString(), eq(PriceEvent.class))).thenReturn(event);
        when(alertRepository.findByStatusAndCoin("ACTIVE", "BTC")).thenReturn(java.util.List.of(alert));
        when(objectMapper.writeValueAsString(any())).thenReturn("email-json");

        kafkaConsumer.consume("ignored");

        verify(kafkaProducer).sendEvent("email-json");
        verify(alertRepository).save(alert);
        assertEquals("COMPLETED", alert.getStatus());
    }

    @Test
    void shouldNotTriggerAlertWhenPriceBelowAlert() throws Exception {
        PriceEvent event = new PriceEvent();
        event.setSymbol("BTC");
        event.setPrice(40000.00);

        Alert alert = new Alert();
        alert.setEmail("Test@email.com");
        alert.setCoin("BTC");
        alert.setStatus("ACTIVE");
        alert.setCondition("RISES_ABOVE");
        alert.setTargetPrice(50000.00);

        when(objectMapper.readValue(anyString(), eq(PriceEvent.class))).thenReturn(event);
        when(alertRepository.findByStatusAndCoin("ACTIVE", "BTC")).thenReturn(List.of(alert));

        kafkaConsumer.consume("ignored");

        verify(kafkaProducer, never()).sendEvent(anyString());
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    void shouldHandleInvalidJson() throws Exception {
        when(objectMapper.readValue(anyString(), eq(PriceEvent.class))).thenThrow(new RuntimeException("Invalid JSON"));

        kafkaConsumer.consume("invalid-json");
        verifyNoInteractions(kafkaProducer);
        verify(alertRepository, never()).findByStatusAndCoin(anyString(), anyString());
    }

    @Test
    void shouldProcessMultipleAlerts() throws Exception {

        PriceEvent event = new PriceEvent();
        event.setSymbol("BTC");
        event.setPrice(60000);

        Alert a1 = new Alert();
        a1.setCoin("BTC");
        a1.setStatus("ACTIVE");
        a1.setCondition("RISES_ABOVE");
        a1.setTargetPrice(50000.00);

        Alert a2 = new Alert();
        a2.setCoin("BTC");
        a2.setStatus("ACTIVE");
        a2.setCondition("RISES_ABOVE");
        a2.setTargetPrice(55000.00);

        when(objectMapper.readValue(anyString(), eq(PriceEvent.class)))
                .thenReturn(event);

        when(objectMapper.writeValueAsString(any(EmailAlertEvent.class)))
                .thenReturn("json");

        when(alertRepository.findByStatusAndCoin("ACTIVE", "BTC"))
                .thenReturn(List.of(a1, a2));

        kafkaConsumer.consume("ignored");

        verify(kafkaProducer, times(2)).sendEvent("json");
        verify(alertRepository, times(2)).save(any(Alert.class));
    }
}
