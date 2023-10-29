package com.example.expense.service;

import com.example.expense.DTO.IncomeRequest;
import com.example.expense.model.Income;
import com.example.expense.model.User;
import com.example.expense.repository.IncomeCategoryRepository;
import com.example.expense.repository.IncomeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    @Autowired
    private final IncomeRepository incomeRepository;
    @Autowired
    private final IncomeCategoryRepository incomeCategoryRepository;
    @Autowired
    private final UserService userService;

    public List<Income> getAllUserIncomes() {
        User authUser = userService.getAuthorizedUser();
        return incomeRepository.findByUserId(authUser.getId());
    }

    public Income createIncome(IncomeRequest incomeRequest) {
        User authUser = userService.getAuthorizedUser();
        if (authUser == null) {
            throw new NullPointerException("Authorization error");
        }

        Income newIncome = new Income();
        newIncome.setAmount(incomeRequest.getAmount());
        newIncome.setDescription(incomeRequest.getDescription());
        newIncome.setDate(incomeRequest.getDate());
        newIncome.setCategory(incomeCategoryRepository.findById(incomeRequest.getCategoryId()).orElseThrow(()-> new EntityNotFoundException("Category not found")));
        newIncome.setUser(authUser);
        return incomeRepository.save(newIncome);
    }


    public Income updateIncome(Long incomeId, IncomeRequest incomeRequest) throws AccessDeniedException {

        User authUser = userService.getAuthorizedUser();

        if (authUser == null) {
            throw new NullPointerException("Authorization error");
        }

        Income existingIncome = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new EntityNotFoundException("Income with id " + incomeId + " not found"));

        if (!existingIncome.getUser().equals(authUser)) {
            throw new AccessDeniedException("You do not have access to modify this data");
        }

        existingIncome.setAmount(incomeRequest.getAmount());
        existingIncome.setDate(incomeRequest.getDate());
        existingIncome.setCategory(incomeCategoryRepository.findById(incomeRequest.getCategoryId()).orElseThrow(()-> new EntityNotFoundException("Category not found")));
        existingIncome.setDescription(incomeRequest.getDescription());

        return incomeRepository.save(existingIncome);
    }

    public void deleteIncome(Long incomeId) throws AccessDeniedException {
        User authUser = userService.getAuthorizedUser();

        if (authUser == null) {
            throw new NullPointerException("Authorization error");
        }

        Income existingIncome = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new EntityNotFoundException("Income with id " + incomeId + " not found"));

        if (!existingIncome.getUser().equals(authUser)) {
            throw new AccessDeniedException("You do not have access to modify this data");
        }

        incomeRepository.delete(existingIncome);
    }

}