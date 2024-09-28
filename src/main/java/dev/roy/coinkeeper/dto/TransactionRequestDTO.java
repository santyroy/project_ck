package dev.roy.coinkeeper.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record TransactionRequestDTO(
        @NotBlank(message = "Transaction type should be 'CREDIT' or 'DEBIT' ")
        @Pattern(regexp = "(CREDIT|DEBIT)")
        String type,
        @NotNull(message = "Transaction amount is mandatory")
        @Positive
        Float amount,
        LocalDateTime date,
        String category,
        String name,
        Float quantity,
        String unit,
        @NotNull(message = "BudgetId is mandatory")
        @Positive
        Integer budgetId,
        @NotNull(message = "UserId is mandatory")
        @Positive
        Integer userId) {
}
