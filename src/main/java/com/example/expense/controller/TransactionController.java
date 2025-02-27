package com.example.expense.controller;

import com.example.expense.DTO.CategoryDTO;
import com.example.expense.DTO.TransactionDTO;
import com.example.expense.DTO.UserPrincipal;
import com.example.expense.model.Transaction;
import com.example.expense.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/by-budget/{budgetId}")
    public ResponseEntity<Page<Transaction>> getTransactionsByBudget(
            @PathVariable Long budgetId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Transaction> transactions = transactionService.getTransactionsByBudget(budgetId, currentUser, pageable);
        return ResponseEntity.ok(transactions);
    }


    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @RequestBody TransactionDTO transactionRequest,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Transaction transaction = transactionService.createTransaction(transactionRequest, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PutMapping("/{transId}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long transId,
            @RequestBody TransactionDTO transactionRequest,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Transaction transaction = transactionService.updateTransaction(transId, transactionRequest, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @DeleteMapping("/{transId}")
    public ResponseEntity<?> deleteTransaction(
            @PathVariable Long transId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {

        transactionService.deleteTransaction(transId, currentUser);
        return ResponseEntity.noContent().build();
    }


}
