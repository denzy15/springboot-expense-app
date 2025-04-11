package com.example.expense.service;

import com.example.expense.DTO.*;
import com.example.expense.DTO.kafka.TransactionEventDTO;
import com.example.expense.enums.OperationType;
import com.example.expense.model.Account;
import com.example.expense.model.Category;
import com.example.expense.model.Transaction;
import com.example.expense.repository.AccountRepository;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.TransactionRepository;
import com.example.expense.utils.BudgetUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    private final KafkaProducerService kafkaProducerService;


    private final BudgetUtils budgetUtils;

    public List<Transaction> getLast10Transactions(Long budgetId) {
        return transactionRepository.findTop10ByAccount_Budget_IdOrderByCreatedAtDesc(budgetId);
    }

    public TransactionResponseDTO createTransaction(TransactionDTO transactionRequest, UserPrincipal currentUser) {
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
        transactionRepository.save(transaction);

        List<Transaction> recentTrans = this.getLast10Transactions(account.getBudget().getId());

        try {
            kafkaProducerService.sendTransactionCreateEvent(new TransactionEventDTO(
                    null, transactionRequest.getAmount(),
                    transactionRequest.getType(), account.getBudget().getId(), null,
                    transactionRequest.getCategoryId(), transactionRequest.getCreatedAt()
            ));
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return new TransactionResponseDTO(recentTrans, transaction);
    }

    public TransactionResponseDTO updateTransaction(Long transactionId, TransactionDTO transactionRequest, UserPrincipal currentUser) {
        Transaction oldTransaction = transactionRepository.findById(transactionId).orElseThrow(
                () -> new EntityNotFoundException("Транзакция не найдена")
        );

        if (!budgetUtils.hasModifyAccess(oldTransaction.getAccount().getBudget().getId(), currentUser.getId())) {
            throw new AccessDeniedException("Недостаточно прав для изменения");
        }

        if (transactionRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(transactionRequest.getCategoryId()).orElse(null);
            oldTransaction.setCategory(category);
        } else {
            oldTransaction.setCategory(null);
        }

        if (transactionRequest.isAdjustBalance()) {
            adjustAccountBalances(oldTransaction, transactionRequest);
        }

        oldTransaction.setType(transactionRequest.getType());
        oldTransaction.setAmount(transactionRequest.getAmount());
        oldTransaction.setDescription(transactionRequest.getDescription());
        oldTransaction.setCreatedAt(transactionRequest.getCreatedAt() != null ? transactionRequest.getCreatedAt() : oldTransaction.getCreatedAt());

        transactionRepository.save(oldTransaction);

        List<Transaction> recentTrans = this.getLast10Transactions(oldTransaction.getAccount().getBudget().getId());
        return new TransactionResponseDTO(recentTrans, oldTransaction);
    }

    private void adjustAccountBalances(Transaction oldTransaction, TransactionDTO newTransaction) {
        Account oldAccount = oldTransaction.getAccount();
        Account newAccount = accountRepository.findById(newTransaction.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Новый счет не найден"));

        BigDecimal oldAmount = oldTransaction.getAmount();
        BigDecimal newAmount = newTransaction.getAmount();

        // Отменяем старый эффект транзакции
        if (oldTransaction.getType() == OperationType.EXPENSE) {
            oldAccount.setBalance(oldAccount.getBalance().add(oldAmount)); // Вернуть деньги
        } else {
            oldAccount.setBalance(oldAccount.getBalance().subtract(oldAmount)); // Отнять доход
        }

        // Применяем новый эффект
        if (newTransaction.getType() == OperationType.EXPENSE) {
            newAccount.setBalance(newAccount.getBalance().subtract(newAmount)); // Вычесть деньги
        } else {
            newAccount.setBalance(newAccount.getBalance().add(newAmount)); // Прибавить доход
        }

        accountRepository.save(oldAccount);
        if (!oldAccount.equals(newAccount)) {
            accountRepository.save(newAccount);
        }
    }


    public List<Transaction> deleteTransaction(Long transactionId, UserPrincipal currentUser) {
        Long budgetId = transactionRepository.findBudgetIdByTransactionId(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Транзакция не найдена"));

        if (!budgetUtils.hasModifyAccess(budgetId, currentUser.getId())) {
            throw new AccessDeniedException("Недостаточно прав");
        }

        transactionRepository.deleteById(transactionId);
        return this.getLast10Transactions(budgetId);
    }

    public Page<Transaction> getTransactionsByBudget(Long budgetId, UserPrincipal currentUser, Pageable pageable) {
        if (!budgetUtils.hasAccessToBudget(budgetId, currentUser.getId())) {
            throw new AccessDeniedException("Недостаточно прав");
        }

        return transactionRepository.findByAccount_Budget_Id(budgetId, pageable);
    }


    public TransactionSummaryResponseDTO getTransactionSummary(Long budgetId, LocalDate startDate, LocalDate endDate, UserPrincipal currentUser) {

        if (!budgetUtils.hasAccessToBudget(budgetId, currentUser.getId())) {
            throw new AccessDeniedException("У вас нет доступа к данному бюджету");
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // Получаем список транзакций
        List<Transaction> transactions = transactionRepository.findByBudgetAndDateRange(budgetId, startDateTime, endDateTime);

        List<Category> budgetCategories = categoryRepository.findByBudgetId(budgetId);

        List<CategorySummaryResponseDTO> incomes = budgetCategories.stream()
                .filter(category -> category.getCategoryType() == OperationType.INCOME)
                .map(category -> new CategorySummaryResponseDTO(category.getId(), category.getName(), OperationType.INCOME, BigDecimal.ZERO))
                .collect(Collectors.toList());

        List<CategorySummaryResponseDTO> expenses = budgetCategories.stream()
                .filter(category -> category.getCategoryType() == OperationType.EXPENSE)
                .map(category -> new CategorySummaryResponseDTO(category.getId(), category.getName(), OperationType.EXPENSE, BigDecimal.ZERO))
                .collect(Collectors.toList());

        incomes.add(new CategorySummaryResponseDTO(0L, "без категории", OperationType.INCOME, BigDecimal.ZERO));
        expenses.add(new CategorySummaryResponseDTO(0L, "без категории", OperationType.EXPENSE, BigDecimal.ZERO));

        for (Transaction transaction : transactions) {
            OperationType operationType = transaction.getType();
            BigDecimal amount = transaction.getAmount();
            Long categoryId = transaction.getCategory() != null ? transaction.getCategory().getId() : 0L;

            List<CategorySummaryResponseDTO> targetList = (operationType == OperationType.EXPENSE) ? expenses : incomes;

            Optional<CategorySummaryResponseDTO> category = targetList.stream()
                    .filter(c -> c.getId().equals(categoryId))
                    .findFirst();

            category.ifPresent(c -> c.setAmount(c.getAmount().add(amount)));
        }

        incomes.removeIf(c -> c.getId() == 0 && c.getAmount().compareTo(BigDecimal.ZERO) == 0);
        expenses.removeIf(c -> c.getId() == 0 && c.getAmount().compareTo(BigDecimal.ZERO) == 0);

        return new TransactionSummaryResponseDTO(incomes, expenses);
    }

}
