package com.pm.alertengineservice.dto;

import jakarta.validation.constraints.*;

public class AlertRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Coin is required")
    @Size(min = 2, max = 10, message = "Coin should be between 2 and 10 characters")
    private String coin;

    @NotNull(message = "Target price is required")
    @Positive(message = "Target price must be a positive number")
    private double targetPrice;

    @NotBlank(message = "Condition is required")
    private String condition;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(double targetPrice) {
        this.targetPrice = targetPrice;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
