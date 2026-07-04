package com.pm.alertengineservice.service;

import com.pm.alertengineservice.coin.SupportedCoin;
import com.pm.alertengineservice.dto.AlertRequestDTO;
import com.pm.alertengineservice.dto.AlertResponseDTO;
import com.pm.alertengineservice.exception.AlertNotFoundException;
import com.pm.alertengineservice.exception.InvalidCoinException;
import com.pm.alertengineservice.mapper.AlertMapper;
import com.pm.alertengineservice.model.Alert;
import com.pm.alertengineservice.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class AlertService {
    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public List<AlertResponseDTO> getAlerts(){
        List<Alert> alerts = alertRepository.findAll();
        return alerts.stream().map(AlertMapper::toResponse).toList();
    }

    public AlertResponseDTO createAlert(AlertRequestDTO alertRequestDTO) {
        Alert newAlert = alertRepository.save(AlertMapper.toEntity(alertRequestDTO));
        return AlertMapper.toResponse(newAlert);
    }

    public AlertResponseDTO updateAlert(UUID id, AlertRequestDTO alertRequestDTO) {

        validateCoin(alertRequestDTO.getCoin());
        Alert alert = alertRepository.findById(id).orElseThrow(() -> new AlertNotFoundException("Alert not found with id: " + id));
        alert.setEmail(alertRequestDTO.getEmail());
        alert.setCoin(alertRequestDTO.getCoin());
        alert.setTargetPrice(alertRequestDTO.getTargetPrice());
        alert.setCondition(alertRequestDTO.getCondition());
        Alert updatedAlert = alertRepository.save(alert);
        return AlertMapper.toResponse(updatedAlert);
    }

    public void deleteAlert(UUID id) {
        alertRepository.deleteById(id);
    }

    private void validateCoin(String coin) {
        if (!SupportedCoin.isSupported(coin)) {
            throw new InvalidCoinException("Coin " + coin + " is not supported. Supported coins: " + Arrays.toString(SupportedCoin.values()) + ".");
        }
    }
}
