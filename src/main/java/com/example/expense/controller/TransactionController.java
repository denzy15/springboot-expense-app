package com.example.expense.controller;

import com.example.expense.DTO.*;
import com.example.expense.model.Transaction;
import com.example.expense.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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


    @GetMapping("/summary")
    public ResponseEntity<TransactionSummaryResponseDTO> getTransactionSummary(
            @RequestParam Long budgetId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        TransactionSummaryResponseDTO summary = transactionService.getTransactionSummary(budgetId, startDate, endDate, currentUser);
        return ResponseEntity.ok(summary);
    }


    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @RequestBody TransactionDTO transactionRequest,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        TransactionResponseDTO transactionResponse = transactionService.createTransaction(transactionRequest, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
    }

    @PutMapping("/{transId}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable Long transId,
            @RequestBody TransactionDTO transactionRequest,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        TransactionResponseDTO transactionResponse = transactionService.updateTransaction(transId, transactionRequest, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
    }

    @DeleteMapping("/{transId}")
    public ResponseEntity<?> deleteTransaction(
            @PathVariable Long transId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {

        List<Transaction> recentTransactions = transactionService.deleteTransaction(transId, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(recentTransactions);
    }


}
