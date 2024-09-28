package dev.roy.coinkeeper.service;

import dev.roy.coinkeeper.dto.TransactionBudgetDTO;
import dev.roy.coinkeeper.dto.TransactionRequestDTO;
import dev.roy.coinkeeper.dto.TransactionResponseDTO;
import dev.roy.coinkeeper.dto.TransactionUserDTO;
import dev.roy.coinkeeper.dto.budgetdetails.BudgetDetailsResponseDTO;
import dev.roy.coinkeeper.dto.budgetdetails.TotalIncomeAndExpense;
import dev.roy.coinkeeper.entity.Budget;
import dev.roy.coinkeeper.entity.Transaction;
import dev.roy.coinkeeper.entity.TransactionType;
import dev.roy.coinkeeper.entity.User;
import dev.roy.coinkeeper.exception.InvalidBudgetException;
import dev.roy.coinkeeper.exception.TransactionNotFoundException;
import dev.roy.coinkeeper.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private static final String INCOME = "income";
    private static final String EXPENSE = "expense";

    private final TransactionRepository transactionRepository;
    private final BudgetService budgetService;
    private final UserService userService;

    public TransactionResponseDTO addTransaction(TransactionRequestDTO dto) {
        Integer budgetId = dto.budgetId();
        Integer userId = dto.userId();
        Budget budget = budgetService.getBudget(budgetId);
        User user = userService.getUser(userId);
        checkIfBudgetExistsForUser(budget, user);
        log.info("Adding transaction for userId {} with budgetId: {} started", userId, budgetId);
        Transaction transaction = new Transaction();
        transaction.setBudget(budget);
        transaction.setUser(user);
        if (null != dto.date()) {
            transaction.setDate(dto.date());
        } else {
            transaction.setDate(LocalDateTime.now());
        }
        transaction.setAmount(dto.amount());
        transaction.setName(dto.name());
        transaction.setQuantity(dto.quantity());
        transaction.setUnit(dto.unit());
        transaction.setType(TransactionType.CREDIT.name().equals(dto.type().toUpperCase()) ? TransactionType.CREDIT : TransactionType.DEBIT);
        if (dto.category() != null) {
            transaction.setCategory(dto.category());
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction for userId {} with budgetId: {} saved", userId, budgetId);
        return new TransactionResponseDTO(savedTransaction.getId(),
                savedTransaction.getType(), savedTransaction.getAmount(),
                savedTransaction.getCategory(), savedTransaction.getName(), savedTransaction.getQuantity(),
                savedTransaction.getUnit(), savedTransaction.getDate(),
                new TransactionBudgetDTO(savedTransaction.getBudget().getId(), savedTransaction.getBudget().getName()),
                new TransactionUserDTO(savedTransaction.getUser().getId(), savedTransaction.getUser().getName())
        );
    }

    public TransactionResponseDTO findTransactionById(Integer transactionId) {
        log.info("Find transaction by id: {}", transactionId);
        Transaction transaction = getTransaction(transactionId);
        return new TransactionResponseDTO(transaction.getId(), transaction.getType(), transaction.getAmount(), transaction.getCategory(),
                transaction.getName(), transaction.getQuantity(), transaction.getUnit(),
                transaction.getDate(), new TransactionBudgetDTO(transaction.getBudget().getId(), transaction.getBudget().getName()),
                new TransactionUserDTO(transaction.getUser().getId(), transaction.getUser().getName()));
    }

    public void deleteTransactionById(Integer transactionId) {
        log.info("Delete transaction for id: {}", transactionId);
        Transaction transaction = getTransaction(transactionId);
        transactionRepository.delete(transaction);
    }

    public TransactionResponseDTO updateTransactionById(Integer transactionId, TransactionRequestDTO dto) {
        Transaction transaction = getTransaction(transactionId);
        log.info("Updating transaction for id: {} started", transactionId);
        if (dto.type() != null) {
            transaction.setType(TransactionType.CREDIT.name().equals(dto.type().toUpperCase()) ? TransactionType.CREDIT : TransactionType.DEBIT);
        }
        if (dto.amount() != null && dto.amount() > 0) {
            transaction.setAmount(dto.amount());
        }
        if (dto.category() != null) {
            transaction.setCategory(dto.category());
        }
        Transaction updatedTransaction = transactionRepository.save(transaction);
        log.info("Updating transaction for id: {} completed", transactionId);
        return new TransactionResponseDTO(updatedTransaction.getId(),
                updatedTransaction.getType(), updatedTransaction.getAmount(),
                updatedTransaction.getCategory(), updatedTransaction.getName(), updatedTransaction.getQuantity(),
                updatedTransaction.getUnit(), updatedTransaction.getDate(),
                new TransactionBudgetDTO(updatedTransaction.getBudget().getId(), updatedTransaction.getBudget().getName()),
                new TransactionUserDTO(updatedTransaction.getUser().getId(), updatedTransaction.getUser().getName())
        );
    }

    public Page<TransactionResponseDTO> findAllTransactions(Integer pageNo, Integer pageSize) {
        PageRequest page = PageRequest.of(pageNo, pageSize);
        Page<Transaction> transactions = transactionRepository.findAll(page);
        return transactions
                .map(transaction -> new TransactionResponseDTO(transaction.getId(),
                        transaction.getType(), transaction.getAmount(),
                        transaction.getCategory(), transaction.getName(), transaction.getQuantity(),
                        transaction.getUnit(), transaction.getDate(),
                        new TransactionBudgetDTO(transaction.getBudget().getId(), transaction.getBudget().getName()),
                        new TransactionUserDTO(transaction.getUser().getId(), transaction.getUser().getName()))
                );
    }

    public Page<TransactionResponseDTO> findAllTransactionsByBudget(Integer budgetId, int pageNo, int pageSize) {
        Budget budget = budgetService.getBudget(budgetId);
        log.info("Find transaction page for budget: {}", budgetId);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "date"));
        Page<Transaction> transactions = transactionRepository.findTransactionByBudget(budget, pageRequest);
        return transactions
                .map(transaction -> new TransactionResponseDTO(transaction.getId(),
                        transaction.getType(), transaction.getAmount(),
                        transaction.getCategory(), transaction.getName(), transaction.getQuantity(),
                        transaction.getUnit(), transaction.getDate(),
                        new TransactionBudgetDTO(transaction.getBudget().getId(), transaction.getBudget().getName()),
                        new TransactionUserDTO(transaction.getUser().getId(), transaction.getUser().getName()))
                );
    }

    public Map<String, Float> getTotalIncomeAndExpenseByBudget(Integer budgetId) {
        Budget budget = budgetService.getBudget(budgetId);
        log.info("Find transaction list for budget: {}", budgetId);
        List<Transaction> transactions = transactionRepository.findTransactionByBudget(budget);
        return getTotalIncomeAndExpenseFromTransactions(transactions);
    }

    public Page<TransactionResponseDTO> findAllTransactionsByUser(Integer userId, int pageNo, int pageSize) {
        User user = userService.getUser(userId);
        log.info("Find transaction page for user: {}", userId);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "date"));
        Page<Transaction> transactions = transactionRepository.findTransactionByUser(user, pageRequest);
        return transactions
                .map(transaction -> new TransactionResponseDTO(transaction.getId(),
                        transaction.getType(), transaction.getAmount(),
                        transaction.getCategory(), transaction.getName(), transaction.getQuantity(),
                        transaction.getUnit(), transaction.getDate(),
                        new TransactionBudgetDTO(transaction.getBudget().getId(), transaction.getBudget().getName()),
                        new TransactionUserDTO(transaction.getUser().getId(), transaction.getUser().getName()))
                );
    }

    public Map<String, Float> getTotalIncomeAndExpenseByUser(Integer userId) {
        User user = userService.getUser(userId);
        List<Transaction> transactions = transactionRepository.findTransactionByUser(user);
        return getTotalIncomeAndExpenseFromTransactions(transactions);
    }

    private Transaction getTransaction(Integer transactionId) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        if (transactionOpt.isEmpty()) {
            throw new TransactionNotFoundException("Transaction with id: " + transactionId + " not found");
        }
        return transactionOpt.get();
    }

    private void checkIfBudgetExistsForUser(Budget budget, User user) {
        boolean ownerCheck = budget.getUser().getId().equals(user.getId());
        boolean memberCheck = budget.getMembers().stream().anyMatch(member -> member.getId().equals(user.getId()));

        if (!ownerCheck && !memberCheck) {
            throw new InvalidBudgetException("Budget Id: " + budget.getId() + " is invalid for User Id: " + user.getId());
        }
    }

    private Map<String, Float> getTotalIncomeAndExpenseFromTransactions(List<Transaction> transactions) {
        Map<String, Float> totalIncomeAndExpenses = new HashMap<>();
        transactions.forEach(transaction -> {
            if (transaction.getType() == TransactionType.CREDIT) {
                Float income = totalIncomeAndExpenses.getOrDefault(INCOME, 0F);
                income += transaction.getAmount();
                totalIncomeAndExpenses.put(INCOME, income);
            } else {
                Float expense = totalIncomeAndExpenses.getOrDefault(EXPENSE, 0F);
                expense += transaction.getAmount();
                totalIncomeAndExpenses.put(EXPENSE, expense);
            }
        });

        return totalIncomeAndExpenses;
    }

    public BudgetDetailsResponseDTO getTransactionSummaryByBudget(Integer budgetId) {
        Budget budget = budgetService.getBudget(budgetId);
        log.info("Find transaction summary for budget: {}", budgetId);
        List<Transaction> transactions = transactionRepository.findTransactionByBudget(budget);

        Map<String, TotalIncomeAndExpense> totalIncomeAndExpenseByUserMap = new HashMap<>();

        // Adding each user's name inside map, this would also skip duplicate names
        transactions.forEach(transaction -> {
            String email = transaction.getUser().getEmail();
            float amount = transaction.getAmount();
            if (totalIncomeAndExpenseByUserMap.containsKey(email)) {
                TotalIncomeAndExpense totalIncomeAndExpense = totalIncomeAndExpenseByUserMap.get(email);
                float existingIncome = totalIncomeAndExpense.income();
                float existingExpense = totalIncomeAndExpense.expense();

                if (transaction.getType() == TransactionType.CREDIT) {
                    existingIncome += amount;
                } else {
                    existingExpense += amount;
                }

                totalIncomeAndExpenseByUserMap.put(email, new TotalIncomeAndExpense(existingIncome, existingExpense));
            } else {
                if (transaction.getType() == TransactionType.CREDIT) {
                    totalIncomeAndExpenseByUserMap.put(email, new TotalIncomeAndExpense(amount, 0F));
                } else {
                    totalIncomeAndExpenseByUserMap.put(email, new TotalIncomeAndExpense(0F, amount));
                }
            }
        });
        Set<String> allBudgetMembers = new HashSet<>();
        allBudgetMembers.add(budget.getUser().getEmail());
        allBudgetMembers.addAll(budget.getMembers().stream().map(User::getEmail).toList());
        return new BudgetDetailsResponseDTO(totalIncomeAndExpenseByUserMap, allBudgetMembers);
    }
}