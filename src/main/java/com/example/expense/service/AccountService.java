package com.example.expense.service;

import com.example.expense.DTO.AccountDTO;
import com.example.expense.model.Account;
import com.example.expense.model.Budget;
import com.example.expense.model.UserReference;
import com.example.expense.repository.AccountRepository;
import com.example.expense.repository.BudgetRepository;
import com.example.expense.utils.BudgetUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final BudgetRepository budgetRepository;

    private final BudgetUtils budgetUtils;

    public Account createAccount(AccountDTO accountRequest, UserReference currentUser) {
        if (accountRepository.existsByBudgetIdAndName(accountRequest.getBudgetId(), accountRequest.getName())) {
            throw new IllegalArgumentException("Счет с таким названием уже существует в этом бюджете.");
        }

        Budget budget = budgetRepository.findById(accountRequest.getBudgetId())
                .orElseThrow(() -> new EntityNotFoundException("Бюджет не найден"));

        if (
                !budgetUtils.hasModifyAccess(budget.getId(), currentUser.getId())
        ) {
            throw new AccessDeniedException("Недостаточно прав");
        }


        Account account = new Account();
        account.setBudget(budget);
        account.setName(accountRequest.getName());
        account.setBalance(accountRequest.getBalance());
        return accountRepository.save(account);
    }

    public Account updateAccount(Long accountId, AccountDTO accountRequest, UserReference currentUser) {

        if (accountRepository.existsByBudgetIdAndName(accountRequest.getBudgetId(), accountRequest.getName())) {
            throw new IllegalArgumentException("Счет с таким названием уже существует в этом бюджете.");
        }

        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new EntityNotFoundException("Счет не найден"));

        if (
                !budgetUtils.hasModifyAccess(accountRequest.getBudgetId(), currentUser.getId())
        ) {
            throw new AccessDeniedException("Недостаточно прав");
        }

        account.setName(accountRequest.getName());
        account.setBalance(accountRequest.getBalance());
        return accountRepository.save(account);
    }

    public void deleteAccount(Long accountId, UserReference currentUser) {

        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new EntityNotFoundException("Счет не найден")
        );

        if (
                !budgetUtils.hasModifyAccess(account.getBudget().getId(), currentUser.getId())
        ) {
            throw new AccessDeniedException("Недостаточно прав");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Нельзя удалить счет с ненулевым балансом. Сначала переведите средства.");
        }

        List<Account> accounts = accountRepository.findByBudgetId(account.getBudget().getId());

        if (accounts.size() <= 1) {
            throw new IllegalStateException("Нельзя удалить последний счет в бюджете.");
        }

        accountRepository.delete(account);

    }

    public List<Account> getAccountsByBudget(Long budgetId) {
        return accountRepository.findByBudgetId(budgetId);
    }


}

