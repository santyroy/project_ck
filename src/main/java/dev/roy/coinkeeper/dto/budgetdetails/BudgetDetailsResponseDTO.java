package dev.roy.coinkeeper.dto.budgetdetails;

import java.util.Map;

public record BudgetDetailsResponseDTO(Map<String, TotalIncomeAndExpense> totalIncomeAndExpensePerUsers) {
}
