package com.example.expense.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class ExpenseRequest {
    @NotNull
    private Double amount;

    @NotNull
    @DateTimeFormat
    private LocalDate date;

    @NotBlank
    private String description;

    @NotNull
    private Long categoryId;

}
