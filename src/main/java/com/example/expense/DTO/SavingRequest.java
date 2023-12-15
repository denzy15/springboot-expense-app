package com.example.expense.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SavingRequest {
    @NotBlank
    private String name;

    @NotNull
    private double amount;

}
