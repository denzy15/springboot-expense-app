package com.example.expense.DTO;

import com.example.expense.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class TransactionResponseDTO {
    private List<Transaction> recentTransactions;
    private Transaction transaction;
}
