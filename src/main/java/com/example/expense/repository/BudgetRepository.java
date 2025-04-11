package com.example.expense.repository;

import com.example.expense.DTO.MigrationBudgetAccessDTO;
import com.example.expense.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByOwnerId(Long ownerId);

    @Query("Select new com.example.expense.DTO.MigrationBudgetAccessDTO(b.id, b.owner.id) from Budget b")
    List<MigrationBudgetAccessDTO> findAllBudgetOwners();
}
