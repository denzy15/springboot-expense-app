package com.example.expense.controller;

import com.example.expense.DTO.AccountBalanceTransferRequestDTO;
import com.example.expense.DTO.AccountDTO;
import com.example.expense.DTO.UserPrincipal;
import com.example.expense.model.Account;
import com.example.expense.model.UserReference;
import com.example.expense.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @GetMapping("/by-budget/{budgetId}")
    public ResponseEntity<List<Account>> getAccountsByBudget(@AuthenticationPrincipal UserPrincipal currentUser, @PathVariable Long budgetId) {
        List<Account> accounts = accountService.getAccountsByBudget(budgetId);

        return ResponseEntity.ok(accounts);
    }


    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody AccountDTO accountRequest,
                                                 @AuthenticationPrincipal UserPrincipal currentUser) {

        UserReference userReference = new UserReference(currentUser.getId(), currentUser.getEmail(), currentUser.getUsername());
        Account account = accountService.createAccount(accountRequest, userReference);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Account> updateAccount(@RequestBody AccountDTO accountRequest,
                                                 @AuthenticationPrincipal UserPrincipal currentUser,
                                                 @PathVariable Long accountId) {
        UserReference userReference = new UserReference(currentUser.getId(), currentUser.getEmail(), currentUser.getUsername());
        Account account = accountService.updateAccount(accountId, accountRequest, userReference);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/transfer/{budgetId}")
    public ResponseEntity<List<Account>> transferBalanceBetweenAccounts(@RequestBody AccountBalanceTransferRequestDTO accountRequest,
                                                 @AuthenticationPrincipal UserPrincipal currentUser,
                                                 @PathVariable Long budgetId) {
        UserReference userReference = new UserReference(currentUser.getId(), currentUser.getEmail(), currentUser.getUsername());
        List<Account> accounts = accountService.transferBalance(budgetId, accountRequest, userReference);
        return ResponseEntity.ok(accounts);
    }



    @PutMapping("/{accountId}/deactivate")
    public ResponseEntity<?> softDeleteAccount(@AuthenticationPrincipal UserPrincipal currentUser,
                                           @PathVariable Long accountId) {
        UserReference userReference = new UserReference(currentUser.getId(), currentUser.getEmail(), currentUser.getUsername());
        accountService.softDeleteAccount(accountId, userReference);
        return ResponseEntity.noContent().build();

    }

}
