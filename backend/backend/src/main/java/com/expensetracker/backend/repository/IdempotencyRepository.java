package com.expensetracker.backend.repository;

import com.expensetracker.backend.model.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRepository extends JpaRepository<IdempotencyKey, String> {
}