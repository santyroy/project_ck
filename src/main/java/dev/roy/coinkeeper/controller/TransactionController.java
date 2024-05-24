package dev.roy.coinkeeper.controller;

import dev.roy.coinkeeper.dto.ApiResponse;
import dev.roy.coinkeeper.dto.TransactionRequestDTO;
import dev.roy.coinkeeper.dto.TransactionResponseDTO;
import dev.roy.coinkeeper.dto.budgetdetails.BudgetDetailsResponseDTO;
import dev.roy.coinkeeper.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transactions")
@Slf4j
public class TransactionController {

    private static final String TRANSACTION_ADDED = "Transaction added";
    private static final String TRANSACTION_FOUND = "Transaction found";
    private static final String TRANSACTION_DELETED = "Transaction deleted";
    private static final String TRANSACTION_UPDATED = "transaction updated";

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse> addTransaction(@Valid @RequestBody TransactionRequestDTO dto) {
        log.info("Adding new transaction for budget started");
        TransactionResponseDTO transactionResponseDTO = transactionService.addTransaction(dto);
        log.info("Adding new transaction for budget completed");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, 201, TRANSACTION_ADDED, transactionResponseDTO));
    }

    @GetMapping("{transactionId}")
    public ResponseEntity<ApiResponse> findTransactionById(@PathVariable Integer transactionId) {
        log.info("Searching for transaction started");
        TransactionResponseDTO transactionResponseDTO = transactionService.findTransactionById(transactionId);
        log.info("Searching for transaction completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, TRANSACTION_FOUND, transactionResponseDTO));
    }

    @DeleteMapping("{transactionId}")
    public ResponseEntity<ApiResponse> deleteTransactionById(@PathVariable Integer transactionId) {
        log.info("Deletion of transaction started");
        transactionService.deleteTransactionById(transactionId);
        log.info("Deletion of transaction completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, TRANSACTION_DELETED, null));
    }

    @PutMapping("{transactionId}")
    public ResponseEntity<ApiResponse> updateTransactionById(@PathVariable Integer transactionId,
                                                             @RequestBody TransactionRequestDTO dto) {
        log.info("Updating transaction started");
        TransactionResponseDTO transactionResponseDTO = transactionService.updateTransactionById(transactionId, dto);
        log.info("Updating transaction completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, TRANSACTION_UPDATED, transactionResponseDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> findAllTransaction(@RequestParam(required = false, defaultValue = "0") int page,
                                                          @RequestParam(required = false, defaultValue = "5") int size) {
        log.info("Fetching all transactions started");
        Page<TransactionResponseDTO> transactionResponseDTO = transactionService.findAllTransactions(page, size);
        log.info("Fetching all transactions completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, TRANSACTION_FOUND, transactionResponseDTO));
    }

    @GetMapping("/budgets/{budgetId}")
    public ResponseEntity<ApiResponse> findAllTransactionByBudget(@PathVariable Integer budgetId,
                                                                  @RequestParam(required = false, defaultValue = "0") int page,
                                                                  @RequestParam(required = false, defaultValue = "5") int size) {
        log.info("Fetching all transactions by budget started");
        Page<TransactionResponseDTO> transactionResponseDTO = transactionService.findAllTransactionsByBudget(budgetId, page, size);
        log.info("Fetching all transactions  by budget completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, TRANSACTION_FOUND, transactionResponseDTO));
    }

    @GetMapping("/total/budgets/{budgetId}")
    public ResponseEntity<ApiResponse> findTotalIncomeAndExpenseByBudget(@PathVariable Integer budgetId) {
        log.info("Fetching total income and expense by budget started");
        Map<String, Float> totalIncomeAndExpenseByBudget = transactionService.getTotalIncomeAndExpenseByBudget(budgetId);
        log.info("Fetching total income and expense by budget ended");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, TRANSACTION_FOUND, totalIncomeAndExpenseByBudget));
    }

    @GetMapping("/summary/budgets/{budgetId}")
    public ResponseEntity<ApiResponse> getTransactionSummaryByBudget(@PathVariable Integer budgetId) {
        log.info("Fetching transaction summary by budget started");
        BudgetDetailsResponseDTO summaryByBudget = transactionService.getTransactionSummaryByBudget(budgetId);
        log.info("Fetching transaction summary by budget ended");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, TRANSACTION_FOUND, summaryByBudget));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse> findAllTransactionByUser(@PathVariable Integer userId,
                                                                  @RequestParam(required = false, defaultValue = "0") int page,
                                                                  @RequestParam(required = false, defaultValue = "5") int size) {
        log.info("Fetching page of transactions by user started");
        Page<TransactionResponseDTO> transactionResponseDTO = transactionService.findAllTransactionsByUser(userId, page, size);
        log.info("Fetching page of transactions  by user completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, TRANSACTION_FOUND, transactionResponseDTO));
    }

    @GetMapping("/total/users/{userId}")
    public ResponseEntity<ApiResponse> findTotalIncomeAndExpenseByUser(@PathVariable Integer userId) {
        log.info("Fetching total income and expense by user started");
        Map<String, Float> totalIncomeAndExpenseByUser = transactionService.getTotalIncomeAndExpenseByUser(userId);
        log.info("Fetching total income and expense by user ended");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, TRANSACTION_FOUND, totalIncomeAndExpenseByUser));
    }
}
