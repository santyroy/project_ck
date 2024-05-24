package dev.roy.coinkeeper.controller;

import dev.roy.coinkeeper.dto.ApiResponse;
import dev.roy.coinkeeper.dto.BudgetRequestDTO;
import dev.roy.coinkeeper.dto.BudgetResponseDTO;
import dev.roy.coinkeeper.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/budgets")
@Slf4j
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<ApiResponse> addBudget(@Valid @RequestBody BudgetRequestDTO dto) {
        log.info("Adding new budget for user started");
        BudgetResponseDTO budgetResponseDTO = budgetService.addBudget(dto);
        log.info("Adding new budget for user completed");
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, 201, "Budget created", budgetResponseDTO));
    }

    @GetMapping("{budgetId}")
    public ResponseEntity<ApiResponse> findBudgetById(@PathVariable Integer budgetId) {
        log.info("Searching budget for user started");
        BudgetResponseDTO budgetResponseDTO = budgetService.findBudgetById(budgetId);
        log.info("Searching budget for user completed");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, 200, "Budget found", budgetResponseDTO));
    }

    @DeleteMapping("{budgetId}")
    public ResponseEntity<ApiResponse> deleteBudgetById(@PathVariable Integer budgetId) {
        log.info("Deletion of budget started");
        budgetService.deleteBudgetById(budgetId);
        log.info("Deletion of budget completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "Budget deleted", null));
    }

    @PutMapping("{budgetId}")
    public ResponseEntity<ApiResponse> updateBudgetById(@PathVariable Integer budgetId,
                                                        @RequestBody BudgetRequestDTO dto) {
        log.info("Updating budget started");
        BudgetResponseDTO budgetResponseDTO = budgetService.updateBudgetById(budgetId, dto);
        log.info("Updating budget completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "Budget updated", budgetResponseDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> findAllBudgets(@RequestParam(required = false, defaultValue = "0") int page,
                                                      @RequestParam(required = false, defaultValue = "5") int size) {
        log.info("Fetching all budgets started");
        Page<BudgetResponseDTO> allBudgets = budgetService.findAllBudgets(page, size);
        log.info("Fetching all budgets completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "Budgets fetched", allBudgets));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse> findAllBudgetsByUser(@PathVariable Integer userId,
                                                            @RequestParam(required = false, defaultValue = "0") int page,
                                                            @RequestParam(required = false, defaultValue = "5") int size,
                                                            @RequestParam(required = false, defaultValue = "false") boolean members) {
        log.info("Fetching all budgets by User started");
        Page<BudgetResponseDTO> allBudgets = budgetService.findAllBudgetsByUser(userId, page, size, members);
        log.info("Fetching all budgets by User completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "Budgets fetched", allBudgets));
    }
}
