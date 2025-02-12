package com.example.expense.controller;

import com.example.expense.DTO.CategoryDTO;
import com.example.expense.DTO.TransactionDTO;
import com.example.expense.DTO.UserPrincipal;
import com.example.expense.model.Transaction;
import com.example.expense.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @RequestBody TransactionDTO transactionRequest,
            @AuthenticationPrincipal UserPrincipal currentUser
    ){
        Transaction transaction = transactionService.createTransaction(transactionRequest, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PutMapping("/{transId}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long transId,
            @RequestBody TransactionDTO transactionRequest,
            @AuthenticationPrincipal UserPrincipal currentUser
    ){
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
