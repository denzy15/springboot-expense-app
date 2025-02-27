package com.example.expense.repository;

import com.example.expense.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
//    List<Transaction> findByAccount_BudgetId(Long budgetId, Pageable pageable);

    List<Transaction> findTop10ByAccount_Budget_IdOrderByCreatedAtDesc(Long budgetId);

    Page<Transaction> findByAccount_Budget_Id(Long budgetId, Pageable pageable);

    @Query("SELECT a.budget.id FROM Transaction t JOIN t.account a WHERE t.id = :transactionId")
    Optional<Long> findBudgetIdByTransactionId(@Param("transactionId") Long transactionId);

}

