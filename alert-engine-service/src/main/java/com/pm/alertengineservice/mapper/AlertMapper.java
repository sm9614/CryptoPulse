package com.pm.alertengineservice.mapper;

import com.pm.alertengineservice.dto.AlertRequestDTO;
import com.pm.alertengineservice.dto.AlertResponseDTO;
import com.pm.alertengineservice.model.Alert;

public class AlertMapper {
    public static Alert toEntity(AlertRequestDTO alertRequestDTO) {
        Alert alert = new Alert();
        alert.setEmail(alertRequestDTO.getEmail());
        alert.setCoin(alert.getCoin());
        alert.setTargetPrice(alertRequestDTO.getTargetPrice());
        alert.setCondition(alertRequestDTO.getCondition());
        alert.setStatus("PENDING"); // Default status when creating a new alert

        return alert;
    }

    public static AlertResponseDTO toResponse(Alert alert) {
        AlertResponseDTO alertResponseDTO = new AlertResponseDTO();
        alertResponseDTO.setId(alert.getId());
        alertResponseDTO.setEmail(alert.getEmail());
        alertResponseDTO.setCoin(alert.getCoin());
        alertResponseDTO.setTargetPrice(alert.getTargetPrice());
        alertResponseDTO.setCondition(alert.getCondition());
        alertResponseDTO.setStatus(alert.getStatus());

        return alertResponseDTO;
    }
}
