package dev.roy.coinkeeper.service;

import dev.roy.coinkeeper.dto.BudgetRequestDTO;
import dev.roy.coinkeeper.dto.BudgetResponseDTO;
import dev.roy.coinkeeper.dto.Member;
import dev.roy.coinkeeper.entity.Budget;
import dev.roy.coinkeeper.entity.User;
import dev.roy.coinkeeper.exception.BudgetNotFoundException;
import dev.roy.coinkeeper.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final UserService userService;
    private final BudgetRepository budgetRepository;

    public BudgetResponseDTO addBudget(BudgetRequestDTO dto) {
        Integer userId = dto.userId();
        log.info("Adding budget for userId: {}", userId);
        User owner = userService.getUser(userId);

        Budget budget = new Budget();
        budget.setName(dto.name());
        budget.setUser(owner);
        budget.setOpenDate(LocalDateTime.now());
        budget.setTransactions(null);

        // Check if payload has non-mandatory fields
        if (dto.type() != null) {
            log.info("Budget type added");
            budget.setType(dto.type());
        }
        if (dto.goal() != null) {
            log.info("Budget goal added");
            budget.setGoal(dto.goal());
        }

        if (dto.memberEmails() != null && !dto.memberEmails().isEmpty()) {
            List<User> filteredMembers = filterMembersExceptOwner(dto, owner);
            budget.setMembers(new ArrayList<>(filteredMembers));
        }
        Budget savedBudget = budgetRepository.save(budget);
        return new BudgetResponseDTO(savedBudget.getId(), savedBudget.getName(), savedBudget.getType(), savedBudget.getGoal(),
                savedBudget.getOpenDate(), userId, getMembers(savedBudget));
    }

    public BudgetResponseDTO findBudgetById(Integer budgetId) {
        Budget budget = getBudget(budgetId);
        return new BudgetResponseDTO(budget.getId(), budget.getName(), budget.getType(), budget.getGoal(),
                budget.getOpenDate(), budget.getUser().getId(), getMembers(budget));
    }

    public void deleteBudgetById(Integer budgetId) {
        Budget budget = getBudget(budgetId);
        budgetRepository.delete(budget);
    }

    public BudgetResponseDTO updateBudgetById(Integer budgetId, BudgetRequestDTO dto) {
        Budget existingBudget = getBudget(budgetId);
        if (dto.name() != null) {
            existingBudget.setName(dto.name());
        }
        if (dto.type() != null) {
            existingBudget.setType(dto.type());
        }
        if (dto.goal() != null) {
            existingBudget.setGoal(dto.goal());
        }
        if (dto.memberEmails() != null) {
            if (dto.memberEmails().isEmpty()) {
                existingBudget.setMembers(new ArrayList<>());
            } else {
                List<User> filteredMembers = filterMembersExceptOwner(dto, existingBudget.getUser());
                existingBudget.setMembers(new ArrayList<>(filteredMembers));
            }
        }
        Budget updatedBudget = budgetRepository.save(existingBudget);
        return new BudgetResponseDTO(updatedBudget.getId(), updatedBudget.getName(), updatedBudget.getType(),
                updatedBudget.getGoal(), updatedBudget.getOpenDate(), updatedBudget.getUser().getId(), getMembers(updatedBudget));
    }

    public Page<BudgetResponseDTO> findAllBudgets(int pageNo, int pageSize) {
        PageRequest page = PageRequest.of(pageNo, pageSize);
        Page<Budget> budgets = budgetRepository.findAll(page);
        return budgets
                .map(budget -> new BudgetResponseDTO(budget.getId(), budget.getName(), budget.getType(),
                        budget.getGoal(), budget.getOpenDate(), budget.getUser().getId(), getMembers(budget)));
    }

    public Page<BudgetResponseDTO> findAllBudgetsByUser(Integer userId, int pageNo, int pageSize, boolean members) {
        User user = userService.getUser(userId);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "openDate"));
        Page<Budget> budgets;
        if (members) {
            budgets = budgetRepository.findByMembers(List.of(user), pageRequest);
        } else {
            budgets = budgetRepository.findByUser(user, pageRequest);
        }
        return budgets
                .map(budget -> new BudgetResponseDTO(budget.getId(), budget.getName(), budget.getType(),
                        budget.getGoal(), budget.getOpenDate(), budget.getUser().getId(), getMembers(budget)));
    }

    protected Budget getBudget(Integer budgetId) {
        Optional<Budget> budgetOpt = budgetRepository.findById(budgetId);
        if (budgetOpt.isEmpty()) {
            throw new BudgetNotFoundException("Budget with id: " + budgetId + " not found");
        }
        return budgetOpt.get();
    }

    private static List<Member> getMembers(Budget budget) {
        if (budget.getMembers() == null || budget.getMembers().isEmpty()) {
            return Collections.emptyList();
        }
        return budget.getMembers().stream().map(user -> new Member(user.getId(), user.getEmail())).toList();
    }

    private List<User> filterMembersExceptOwner(BudgetRequestDTO dto, User owner) {
        List<User> members = dto.memberEmails().stream().map(userService::getUser).toList();
        // A check to filter out members excluding the owner
        return members.stream().filter(user -> !user.equals(owner)).toList();
    }
}
