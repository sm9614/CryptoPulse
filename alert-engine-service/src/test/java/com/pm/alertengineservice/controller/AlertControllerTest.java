package com.pm.alertengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.alertengineservice.dto.AlertRequestDTO;
import com.pm.alertengineservice.dto.AlertResponseDTO;
import com.pm.alertengineservice.service.AlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebMvcTest(AlertController.class)
public class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AlertService alertService;

    @Test
    void shouldReturnAllAlerts() throws Exception{
        AlertResponseDTO alert = new AlertResponseDTO();
        alert.setId(UUID.randomUUID());
        alert.setCoin("ETH");
        alert.setEmail("email@email.com");
        alert.setTargetPrice(1000.00);
        alert.setCondition("DROPS_BELOW");
        alert.setStatus("ACTIVE");

        when(alertService.getAlerts()).thenReturn(List.of(alert));
        mockMvc.perform(get("/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(alertService).getAlerts();
    }

    @Test
    void shouldCreateAlert() throws Exception {
       AlertRequestDTO request = new AlertRequestDTO();
       request.setCoin("ETH");
       request.setEmail("test@email.com");
       request.setTargetPrice(2000.00);
       request.setCondition("RISES_ABOVE");

       AlertResponseDTO response = new AlertResponseDTO();
       response.setId(UUID.randomUUID());
       response.setCoin("ETH");
       response.setEmail("test@email.com");
       response.setTargetPrice(2000.00);
       response.setCondition("RISES_ABOVE");
       response.setStatus("ACTIVE");

       when(alertService.createAlert(any(AlertRequestDTO.class))).thenReturn(response);

       mockMvc.perform(post("/alerts")
               .contentType(APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated());

       verify(alertService).createAlert(any(AlertRequestDTO.class));
    }

    @Test
    void shouldUpdateAlert() throws Exception {
        UUID id = UUID.randomUUID();

        AlertRequestDTO request = new AlertRequestDTO();
        request.setCoin("ETH");
        request.setEmail("test@email.com");
        request.setTargetPrice(1500.00);
        request.setCondition("RISES_ABOVE");

        AlertResponseDTO response = new AlertResponseDTO();
        response.setId(id);
        response.setCoin("ETH");
        response.setEmail("test@email.com");
        response.setTargetPrice(1500.00);
        response.setCondition("RISES_ABOVE");
        response.setStatus("ACTIVE");

        when(alertService.updateAlert(eq(id), any(AlertRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/alerts/{id}", id)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(alertService).updateAlert(eq(id), any(AlertRequestDTO.class));
    }

    @Test
    void shouldDeleteAlert() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(alertService).deleteAlert(id);

        mockMvc.perform(delete("/alerts/{id}", id))
                .andExpect(status().isNoContent());

        verify(alertService).deleteAlert(id);
    }
}
