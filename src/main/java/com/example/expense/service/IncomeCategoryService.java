package com.example.expense.service;

import com.example.expense.DTO.IncomeCategoryRequest;
import com.example.expense.model.IncomeCategory;
import com.example.expense.model.User;
import com.example.expense.repository.IncomeCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeCategoryService {
    @Autowired
    private final IncomeCategoryRepository incomeCategoryRepository;
    @Autowired
    private final UserService userService;


    public List<IncomeCategory> getAllUserIncomeCategories() {
        User authUser = userService.getAuthorizedUser();
        return incomeCategoryRepository.findByUserId(authUser.getId());
    }

    public IncomeCategory createIncomeCategory(IncomeCategoryRequest categoryRequest) {
        User authUser = userService.getAuthorizedUser();

        if (authUser == null) {
            throw new NullPointerException("Authorization error");
        }

        IncomeCategory newIncCategory = new IncomeCategory();
        newIncCategory.setName(categoryRequest.getName());
        newIncCategory.setUser(authUser);
        return incomeCategoryRepository.save(newIncCategory);
    }


    public IncomeCategory updateIncomeCategory(Long icId, IncomeCategoryRequest categoryRequest) throws AccessDeniedException {
        User authUser = userService.getAuthorizedUser();

        if (authUser == null) {
            throw new NullPointerException("Authorization error");
        }

        IncomeCategory existingIncomeCategory = incomeCategoryRepository.findById(icId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Income category not found with id " + icId)
                );

        if (!existingIncomeCategory.getUser().equals(authUser)) {
            throw new AccessDeniedException("You do not have access to modify this data");
        }

        existingIncomeCategory.setName(categoryRequest.getName());
        return incomeCategoryRepository.save(existingIncomeCategory);
    }

    public void deleteIncomeCategory(Long icId) throws AccessDeniedException {
        User authUser = userService.getAuthorizedUser();

        if (authUser == null) {
            throw new NullPointerException("Authorization error");
        }

        IncomeCategory existingIncomeCategory =
                incomeCategoryRepository.findById(icId).orElseThrow(() -> new EntityNotFoundException("Income " +
                        "category not found with id " + icId));

        if (!existingIncomeCategory.getUser().equals(authUser)) {
            throw new AccessDeniedException("You do not have access to modify this data");
        }

        incomeCategoryRepository.delete(existingIncomeCategory);
    }

}