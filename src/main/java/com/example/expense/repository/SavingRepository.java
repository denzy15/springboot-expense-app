package com.example.expense.repository;

import com.example.expense.model.Saving;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingRepository extends JpaRepository<Saving, Long> {
    List<Saving> findByUserId(Long userId);
}
