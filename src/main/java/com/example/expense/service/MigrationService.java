package com.example.expense.service;

import com.example.expense.DTO.MigrationBudgetAccessDTO;
import com.example.expense.DTO.MigrationCategoryDTO;
import com.example.expense.DTO.MigrationTransDTO;
import com.example.expense.DTO.MigrationTransProjection;
import com.example.expense.enums.OperationType;
import com.example.expense.model.BudgetMember;
import com.example.expense.model.Category;
import com.example.expense.model.Transaction;
import com.example.expense.repository.BudgetMemberRepository;
import com.example.expense.repository.BudgetRepository;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MigrationService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetMemberRepository budgetMemberRepository;
    private final BudgetRepository budgetRepository;

    public List<MigrationTransDTO> getAllTransactions() {
        List<MigrationTransProjection> transProjections = transactionRepository.findMigrationTransactions();

        return transProjections.stream().map(p -> new MigrationTransDTO(
                p.getTrDate(),
                p.getBudgetId(),
                p.getTotalAmount(),
                OperationType.valueOf(p.getTrType()), // тут нужно приведение строки к enum
                p.getCategoryId(),
                p.getTrCount()
        )).toList();

    }

    public List<MigrationCategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(Category::convertToMigrationDTO)
                .toList();
    }

    public List<MigrationBudgetAccessDTO> getAllAccessData(){
        List<MigrationBudgetAccessDTO> budgetMembers = budgetMemberRepository.findAll()
                .stream()
                .map(BudgetMember::convertToMigrationDTO)
                .toList();

        List<MigrationBudgetAccessDTO> budgetOwners = budgetRepository.findAllBudgetOwners();

        List<MigrationBudgetAccessDTO> accessList = new ArrayList<>(budgetMembers);
        accessList.addAll((budgetOwners));

        return accessList;
    }
}
