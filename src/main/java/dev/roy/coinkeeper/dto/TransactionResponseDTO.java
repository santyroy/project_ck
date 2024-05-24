package dev.roy.coinkeeper.dto;

import dev.roy.coinkeeper.entity.TransactionType;

import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Integer id,
        TransactionType type,
        Float amount,
        String category,
        LocalDateTime date,
        TransactionBudgetDTO budget,
        TransactionUserDTO user) {
}
