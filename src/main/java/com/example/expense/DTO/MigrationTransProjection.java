package com.example.expense.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface MigrationTransProjection {
    LocalDate getTrDate();
    Long getBudgetId();
    BigDecimal getTotalAmount();
    String getTrType();
    Long getCategoryId();
    Integer getTrCount();
}

