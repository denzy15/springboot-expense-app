package com.example.expense.repository;

import com.example.expense.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByBudgetIdAndName(Long budgetId, String name);

    boolean existsByBudgetIdAndNameAndIdNot(Long budgetId, String name, Long id);


    @Query("SELECT a FROM Account a WHERE a.budget.id = :budgetId AND a.status = 'A'")
    List<Account> findActiveAccountsByBudgetId(@Param("budgetId") Long budgetId);

    // Удалить? Возвращает и активные и удаленные записи
    List<Account> findByBudgetId(Long budgetId);

//    @Query("SELECT a FROM Account a WHERE a.budget.id = :budgetId")
//    List<Account> findByBudgetId(@Param("budgetId") Long budgetId);

}

