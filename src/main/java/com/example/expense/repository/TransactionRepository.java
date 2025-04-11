package com.example.expense.repository;

import com.example.expense.DTO.MigrationTransDTO;
import com.example.expense.DTO.MigrationTransProjection;
import com.example.expense.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
//    List<Transaction> findByAccount_BudgetId(Long budgetId, Pageable pageable);

    List<Transaction> findTop10ByAccount_Budget_IdOrderByCreatedAtDesc(Long budgetId);

    Page<Transaction> findByAccount_Budget_Id(Long budgetId, Pageable pageable);

    @Query("SELECT a.budget.id FROM Transaction t JOIN t.account a WHERE t.id = :transactionId")
    Optional<Long> findBudgetIdByTransactionId(@Param("transactionId") Long transactionId);

    @Query("SELECT t FROM Transaction t WHERE t.account.budget.id = :budgetId AND t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findByBudgetAndDateRange(@Param("budgetId") Long budgetId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT CAST(t.created_at AS DATE) AS trDate, " +
            "c.budget_id AS budgetId, " +
            "t.type AS trType, " +
            "SUM(t.amount) AS totalAmount, " +
            "COUNT(*) AS trCount, " +
            "c.id AS categoryId " +
            "FROM transactions t " +
            "LEFT JOIN categories c ON c.id = t.category_id " +
            "GROUP BY c.budget_id, CAST(t.created_at AS DATE), c.id, c.name, t.type " +
            "ORDER BY budget_id, CAST(t.created_at AS DATE)", nativeQuery = true)
    List<MigrationTransProjection> findMigrationTransactions();
}

