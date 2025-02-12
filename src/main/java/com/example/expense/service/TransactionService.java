package com.example.expense.service;

import com.example.expense.DTO.TransactionDTO;
import com.example.expense.DTO.UserPrincipal;
import com.example.expense.enums.OperationType;
import com.example.expense.model.Account;
import com.example.expense.model.Category;
import com.example.expense.model.Transaction;
import com.example.expense.repository.AccountRepository;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.TransactionRepository;
import com.example.expense.utils.BudgetUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    private final BudgetUtils budgetUtils;

    public Transaction createTransaction(TransactionDTO transactionRequest, UserPrincipal currentUser) {
        Transaction transaction = new Transaction();

        Account account = accountRepository.findById(transactionRequest.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Счет не найден"));

        if (!budgetUtils.hasModifyAccess(account.getBudget().getId(), currentUser.getId())) {
            throw new AccessDeniedException("Недостаточно прав");
        }

        if (transactionRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(transactionRequest.getCategoryId()).orElse(null);
            transaction.setCategory(category);
        } else {
            transaction.setCategory(null);
        }

        transaction.setAccount(account);
        transaction.setType(transactionRequest.getType());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setDescription(transactionRequest.getDescription());
        transaction.setCreatedAt(transactionRequest.getCreatedAt() != null ? transactionRequest.getCreatedAt() : LocalDateTime.now());

        if (transactionRequest.getType() == OperationType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
        } else {
            account.setBalance(account.getBalance().add(transactionRequest.getAmount()));
        }

        accountRepository.save(account);
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long transactionId, TransactionDTO transactionRequest, UserPrincipal currentUser) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(
                () -> new EntityNotFoundException("Транзакция не найдена")
        );

        Account newAccount = accountRepository.findById(transactionRequest.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Счет не найден"));

        if (!budgetUtils.hasModifyAccess(newAccount.getBudget().getId(), currentUser.getId())) {
            throw new AccessDeniedException("Недостаточно прав");
        }

        if (transactionRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(transactionRequest.getCategoryId()).orElse(null);
            transaction.setCategory(category);
        } else {
            transaction.setCategory(null);
        }

        if (!Objects.equals(transaction.getAccount().getId(), newAccount.getId())) {
            Account prevAccount = accountRepository.findById(transaction.getAccount().getId()).orElse(null);

            transaction.setAccount(newAccount);

            if (transactionRequest.getType() == OperationType.EXPENSE) {
                newAccount.setBalance(newAccount.getBalance().subtract(transactionRequest.getAmount()));

                if (prevAccount != null)
                    prevAccount.setBalance(prevAccount.getBalance().add(transactionRequest.getAmount()));

            } else {
                newAccount.setBalance(newAccount.getBalance().add(transactionRequest.getAmount()));

                if (prevAccount != null)
                    prevAccount.setBalance(prevAccount.getBalance().subtract(transactionRequest.getAmount()));
            }

            if (prevAccount != null) accountRepository.save(prevAccount);
        }

        transaction.setType(transactionRequest.getType());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setDescription(transactionRequest.getDescription());
        transaction.setCreatedAt(transactionRequest.getCreatedAt() != null ? transactionRequest.getCreatedAt() : transaction.getCreatedAt());

        accountRepository.save(newAccount);
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long transactionId, UserPrincipal currentUser) {
        Long budgetId = transactionRepository.findBudgetIdByTransactionId(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Транзакция не найдена"));

        if (!budgetUtils.hasModifyAccess(budgetId, currentUser.getId())) {
            throw new AccessDeniedException("Недостаточно прав");
        }

        transactionRepository.deleteById(transactionId);
    }

    public List<Transaction> getTransactionsByBudget(Long budgetId) {
        return transactionRepository.findByAccount_BudgetId(budgetId);
    }
}
