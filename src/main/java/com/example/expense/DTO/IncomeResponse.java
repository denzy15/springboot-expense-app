package com.example.expense.DTO;

import com.example.expense.model.Income;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class IncomeResponse {
    private Long id;
    private Double amount;
    private LocalDate date;
    private String description;
    private IncomeCategoryResponse category;

    public static IncomeResponse convertOne(Income income) {
        return new IncomeResponse(income.getId(), income.getAmount(), income.getDate(), income.getDescription(),
                new IncomeCategoryResponse(income.getCategory().getId(),income.getCategory().getName()));

    }

    public static List<IncomeResponse> convertMany(List<Income> incomes) {
        return incomes.stream()
                .map(IncomeResponse::convertOne)
                .collect(Collectors.toList());
    }

}
