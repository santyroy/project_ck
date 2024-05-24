package dev.roy.coinkeeper.dto;

import jakarta.validation.constraints.*;

public record ResetPasswordRequestDTO(
        @NotBlank(message = "Email is mandatory")
        @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        String email,
        @Positive
        @NotNull
        Integer otp,
        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, message = "Password length: min 8 characters")
        String password) {
}
