package dev.roy.coinkeeper.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VerifyOTPRequestDTO(
        @Positive
        @NotNull
        Integer otp,
        @Positive
        @NotNull
        Integer userId) {
}
