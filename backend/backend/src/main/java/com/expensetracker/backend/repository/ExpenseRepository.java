package com.expensetracker.backend.repository;

import com.expensetracker.backend.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    // For filtering by category (used later)
    List<Expense> findByCategory(String category);
}