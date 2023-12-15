package com.example.expense.DTO;

import com.example.expense.model.Saving;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class SavingResponse {
    private Long id;
    private String name;
    private double amount;


    public static SavingResponse convertOne(Saving saving) {
        return new SavingResponse(saving.getId(), saving.getName(), saving.getAmount());
    }

    public static List<SavingResponse> convertMany(List<Saving> savings) {
        return savings.stream().map(
                SavingResponse::convertOne
        ).collect(Collectors.toList());
    }

}
