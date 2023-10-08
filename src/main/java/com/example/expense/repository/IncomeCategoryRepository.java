package com.example.expense.repository;

import com.example.expense.model.Income;
import com.example.expense.model.IncomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Long> {
    List<IncomeCategory> findByUserId(Long userId);
    IncomeCategory findByNameAndUserId(String name, Long userId);


}
