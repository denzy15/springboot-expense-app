package com.example.expense.DTO;

import com.example.expense.model.IncomeCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class IncomeCategoryResponse {
    private Long id;
    private String name;

    public static IncomeCategoryResponse convertOne(IncomeCategory incomeCategory) {
        return new IncomeCategoryResponse(incomeCategory.getId(), incomeCategory.getName());
    }

    public static List<IncomeCategoryResponse> convertMany(List<IncomeCategory> incomeCategories) {
        return incomeCategories.stream()
                .map(IncomeCategoryResponse::convertOne)
                .collect(Collectors.toList());
    }
}
