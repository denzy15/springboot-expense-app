package com.example.expense.service;

import com.example.expense.model.IncomeCategory;
import com.example.expense.repository.IncomeCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeCategoryService {
    private final IncomeCategoryRepository incomeCategoryRepository;

    @Autowired
    public IncomeCategoryService(IncomeCategoryRepository incomeCategoryRepository) {
        this.incomeCategoryRepository = incomeCategoryRepository;
    }

    public IncomeCategory createIncomeCategory(IncomeCategory category) {
        return incomeCategoryRepository.save(category);
    }

    public List<IncomeCategory> getAllIncomeCategoriesByUserId(Long userId) {
        return incomeCategoryRepository.findByUserId(userId);
    }


    public IncomeCategory updateIncomeCategory(IncomeCategory category){
        IncomeCategory existingIncomeCategory = incomeCategoryRepository.findById(category.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Income category not found with id " + category.getId())
                );
        existingIncomeCategory.setName(category.getName());
        return incomeCategoryRepository.save(existingIncomeCategory);
    }

    public void deleteIncomeCategory(Long categoryId){
        IncomeCategory existingIncomeCategory = incomeCategoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Income category not found with id " + categoryId)
                );
        incomeCategoryRepository.delete(existingIncomeCategory);
    }

}