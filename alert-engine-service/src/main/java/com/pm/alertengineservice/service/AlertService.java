package com.pm.alertengineservice.service;

import com.pm.alertengineservice.repository.AlertRepository;
import org.springframework.stereotype.Service;

@Service
public class AlertService {
    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }
}
