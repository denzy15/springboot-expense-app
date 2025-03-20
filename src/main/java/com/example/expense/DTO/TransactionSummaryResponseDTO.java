package com.example.expense.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class TransactionSummaryResponseDTO
{
    private List<CategorySummaryResponseDTO> incomes;
    private List<CategorySummaryResponseDTO> expenses;
}
