package com.expensetracker.backend.controller;

import com.expensetracker.backend.dto.CreateExpenseRequest;
import com.expensetracker.backend.dto.ExpenseListResponse;
import com.expensetracker.backend.dto.ExpenseResponse;
import com.expensetracker.backend.model.Expense;
import com.expensetracker.backend.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ExpenseResponse createExpense(
            @RequestBody CreateExpenseRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String key
    ) {

        if (key == null) {
            throw new RuntimeException("Idempotency-Key header is required");
        }

        Expense expense = Expense.builder()
                .amount(request.getAmount())
                .category(request.getCategory())
                .description(request.getDescription())
                .date(request.getDate())
                .build();

        return expenseService.createExpenseWithIdempotency(expense, key);
    }

    @GetMapping
    public ExpenseListResponse getExpenses(
            @RequestParam(required = false) String category
    ) {
        return expenseService.getExpenses(category);
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable String id) {
        expenseService.deleteExpense(UUID.fromString(id));
    }
}