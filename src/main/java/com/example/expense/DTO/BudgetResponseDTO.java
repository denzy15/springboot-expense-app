package com.example.expense.DTO;

import com.example.expense.model.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BudgetResponseDTO {
    private Long id;
    private String name;
    private UserReference owner;

    private boolean shared;

    private List<Account> accounts;

    private List<Category> categories;

    private List<BudgetMember> members;

    private List<Transaction> recentTransactions;
}
