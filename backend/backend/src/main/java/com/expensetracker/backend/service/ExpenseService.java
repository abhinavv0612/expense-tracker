package com.expensetracker.backend.service;

import com.expensetracker.backend.dto.ExpenseListResponse;
import com.expensetracker.backend.dto.ExpenseResponse;
import com.expensetracker.backend.model.Expense;
import com.expensetracker.backend.model.IdempotencyKey;
import com.expensetracker.backend.repository.ExpenseRepository;
import com.expensetracker.backend.repository.IdempotencyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;

    public ExpenseResponse createExpenseWithIdempotency(
            Expense expense,
            String idempotencyKey
    ) {
        try {
            // 1. Check if key exists
            if (idempotencyRepository.existsById(idempotencyKey)) {
                IdempotencyKey existing = idempotencyRepository.findById(idempotencyKey).get();

                return objectMapper.readValue(existing.getResponse(), ExpenseResponse.class);
            }

            // 2. Save new expense
            Expense saved = expenseRepository.save(expense);

            ExpenseResponse response = ExpenseResponse.builder()
                    .id(saved.getId())
                    .amount(saved.getAmount())
                    .category(saved.getCategory())
                    .description(saved.getDescription())
                    .date(saved.getDate())
                    .build();

            // 3. Store response
            String json = objectMapper.writeValueAsString(response);

            IdempotencyKey entity = IdempotencyKey.builder()
                    .key(idempotencyKey)
                    .response(json)
                    .createdAt(Instant.now())
                    .build();

            idempotencyRepository.save(entity);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Idempotency failed", e);
        }
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<Expense> getExpensesByCategory(String category) {
        return expenseRepository.findByCategory(category);
    }

        public ExpenseListResponse getExpenses(String category) {

            List<Expense> expenses;

            if (category != null) {
                expenses = expenseRepository.findByCategory(category);
            } else {
                expenses = expenseRepository.findAll();
            }

            // Sort by date DESC (newest first)
            expenses = expenses.stream()
                    .sorted(Comparator.comparing(Expense::getDate).reversed())
                    .toList();

            // Convert to DTO
            List<ExpenseResponse> responseList = expenses.stream()
                    .map(exp -> ExpenseResponse.builder()
                            .id(exp.getId())
                            .amount(exp.getAmount())
                            .category(exp.getCategory())
                            .description(exp.getDescription())
                            .date(exp.getDate())
                            .build())
                    .collect(Collectors.toList());

            // Calculate total
            BigDecimal total = expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return ExpenseListResponse.builder()
                    .expenses(responseList)
                    .total(total)
                    .build();
        }
    public void deleteExpense(UUID id) {
        expenseRepository.deleteById(id);
    }
}