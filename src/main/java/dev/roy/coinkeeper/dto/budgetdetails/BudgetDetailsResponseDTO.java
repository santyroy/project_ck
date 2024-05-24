package dev.roy.coinkeeper.dto.budgetdetails;

import java.util.Map;
import java.util.Set;

public record BudgetDetailsResponseDTO(
        Map<String, TotalIncomeAndExpense> totalIncomeAndExpensePerUsers,
        Set<String> allBudgetMembers) {
}
