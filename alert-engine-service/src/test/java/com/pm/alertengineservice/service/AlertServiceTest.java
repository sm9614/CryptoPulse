package com.pm.alertengineservice.service;

import com.pm.alertengineservice.dto.AlertRequestDTO;
import com.pm.alertengineservice.dto.AlertResponseDTO;
import com.pm.alertengineservice.exception.AlertNotFoundException;
import com.pm.alertengineservice.model.Alert;
import com.pm.alertengineservice.repository.AlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    @Test
    void shouldReturnAllAlerts() {
        Alert alert = new Alert();
        alert.setId(UUID.randomUUID());
        alert.setEmail("Test@email.com");
        alert.setCoin("BTC");
        alert.setTargetPrice(50000.00);
        alert.setCondition("DROPS_BELOW");
        alert.setStatus("PENDING");

        when(alertRepository.findAll()).thenReturn(List.of(alert));

        List<AlertResponseDTO> alerts = alertService.getAlerts();

        assertEquals(1, alerts.size());
        assertEquals("BTC", alerts.getFirst().getCoin());

        verify(alertRepository).findAll();
    }

    @Test
    void shouldCreateAlert() {
        AlertRequestDTO request = new AlertRequestDTO();
        request.setEmail("Test@email.com");
        request.setCoin("BTC");
        request.setTargetPrice(50000.00);
        request.setCondition("DROPS_BELOW");

        Alert alert = new Alert();
        UUID id = UUID.randomUUID();
        alert.setId(id);
        alert.setEmail("Test@email.com");
        alert.setCoin("BTC");
        alert.setTargetPrice(50000.00);
        alert.setCondition("DROPS_BELOW");
        alert.setStatus("PENDING");

        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        AlertResponseDTO response = alertService.createAlert(request);

        assertEquals(id, response.getId());
        assertEquals("BTC", response.getCoin());
        assertEquals(50000.00, response.getTargetPrice());
        assertEquals("DROPS_BELOW", response.getCondition());
        assertEquals("PENDING", response.getStatus());
        assertEquals("Test@email.com", response.getEmail());

        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void shouldUpdateAlert() {
        Alert alert = new Alert();
        UUID id = UUID.randomUUID();
        alert.setId(id);
        alert.setEmail("Test@email.com");
        alert.setCoin("BTC");
        alert.setTargetPrice(50000.00);
        alert.setCondition("DROPS_BELOW");
        alert.setStatus("PENDING");

        AlertRequestDTO request = new AlertRequestDTO();
        request.setEmail("new@email.com");
        request.setCoin("ETH");
        request.setTargetPrice(1000.00);
        request.setCondition("RISES_ABOVE");

        when(alertRepository.findById(id)).thenReturn(Optional.of(alert));

        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        AlertResponseDTO response = alertService.updateAlert(id, request);

        assertEquals("ETH", response.getCoin());
        assertEquals(1000.00, response.getTargetPrice());
        assertEquals("RISES_ABOVE", response.getCondition());
        assertEquals("new@email.com", response.getEmail());

        verify(alertRepository).findById(id);
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void shouldThrowExceptionWhenNoAlertFound() {
        UUID id = UUID.randomUUID();

        AlertRequestDTO request = new AlertRequestDTO();

        when(alertRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AlertNotFoundException.class, () -> alertService.updateAlert(id, request));

        verify(alertRepository).findById(id);
    }

    @Test
    void shouldDeleteAlert() {
        UUID id = UUID.randomUUID();
        alertService.deleteAlert(id);
        verify(alertRepository).deleteById(id);
    }
}
