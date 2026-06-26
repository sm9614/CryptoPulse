package com.pm.alertengineservice.controller;

import com.pm.alertengineservice.dto.AlertRequestDTO;
import com.pm.alertengineservice.dto.AlertResponseDTO;
import com.pm.alertengineservice.service.AlertService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/alerts")
public class AlertController {
    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<List<AlertResponseDTO>> getAlerts() {
        List<AlertResponseDTO> alerts = alertService.getAlerts();
        return ResponseEntity.ok().body(alerts);
    }

    @PostMapping
    public ResponseEntity<AlertResponseDTO> createAlert(@Validated @RequestBody AlertRequestDTO alertRequestDTO) {

        AlertResponseDTO alertResponseDTO = alertService.createAlert(alertRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(alertResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertResponseDTO> updateAlert(
            @PathVariable UUID id,
            @Validated @RequestBody AlertRequestDTO alertRequestDTO) {

        AlertResponseDTO alertResponseDTO = alertService.updateAlert(id, alertRequestDTO);
        return ResponseEntity.ok().body(alertResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable UUID id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }
}
