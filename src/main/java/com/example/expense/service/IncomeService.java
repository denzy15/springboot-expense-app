package com.example.expense.service;

import com.example.expense.model.Income;
import com.example.expense.repository.IncomeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeService {
    private final IncomeRepository incomeRepository;

    @Autowired
    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public Income createIncome(Income income) {
        return incomeRepository.save(income);
    }

    public List<Income> getAllIncomesByUserId(Long userId) {
        return incomeRepository.findByUserId(userId);
    }

    public Income updateIncome(Income updatedIncome) {
        Income existingIncome = incomeRepository.findById(updatedIncome.getId())
                .orElseThrow(() -> new EntityNotFoundException("Income with id " + updatedIncome.getId() + " not found"));

        existingIncome.setAmount(updatedIncome.getAmount());
        existingIncome.setDate(updatedIncome.getDate());
        existingIncome.setCategory(updatedIncome.getCategory());
        existingIncome.setDescription(updatedIncome.getDescription());

        return incomeRepository.save(existingIncome);
    }

    public void deleteIncome(Long incomeId) {
        Income existingIncome = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new EntityNotFoundException("Income with id " + incomeId + " not found"));
        incomeRepository.delete(existingIncome);
    }

}