package com.example.expense.service;

import com.example.expense.model.IncomeCategory;
import com.example.expense.repository.IncomeCategoryRepository;
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

}