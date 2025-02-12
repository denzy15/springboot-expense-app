package com.example.expense.controller;

import com.example.expense.DTO.AccountDTO;
import com.example.expense.DTO.UserPrincipal;
import com.example.expense.model.Account;
import com.example.expense.model.UserReference;
import com.example.expense.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

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

    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal UserPrincipal currentUser,
                                           @PathVariable Long accountId) {
        UserReference userReference = new UserReference(currentUser.getId(), currentUser.getEmail(), currentUser.getUsername());
        accountService.deleteAccount(accountId, userReference);
        return ResponseEntity.noContent().build();

    }

}
