package dev.roy.coinkeeper.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ResendOTPRequestDTO(
        @Positive
        @NotNull
        Integer userId) {
}
