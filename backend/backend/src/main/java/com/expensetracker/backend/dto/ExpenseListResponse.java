package com.expensetracker.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ExpenseListResponse {
    private List<ExpenseResponse> expenses;
    private BigDecimal total;
}