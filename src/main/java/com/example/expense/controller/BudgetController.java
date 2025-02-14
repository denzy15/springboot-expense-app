package com.example.expense.controller;

import com.example.expense.DTO.BudgetDTO;
import com.example.expense.DTO.BudgetMemberDTO;
import com.example.expense.DTO.UserPrincipal;
import com.example.expense.model.Budget;
import com.example.expense.model.BudgetMember;
import com.example.expense.model.UserReference;
import com.example.expense.service.BudgetMemberService;
import com.example.expense.service.BudgetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;
    private final BudgetMemberService budgetMemberService;

    @GetMapping
    public ResponseEntity<List<Budget>> getUserBudgets(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<Budget> budgetList = budgetService.getBudgetsByUser(currentUser.getId());
        return ResponseEntity.ok(budgetList);
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<Budget> getBudgetById(@AuthenticationPrincipal UserPrincipal currentUser, @PathVariable Long budgetId) {
        Budget budget = budgetService.getBudgetById(budgetId, currentUser);
        return ResponseEntity.ok(budget);
    }

    @PostMapping
    public ResponseEntity<Budget> createBudget(@AuthenticationPrincipal UserPrincipal currentUser, @RequestBody BudgetDTO budgetRequest) {

        Budget newBudget = budgetService.createBudget(currentUser, budgetRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(newBudget);
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<Budget> updateBudget(@AuthenticationPrincipal UserPrincipal currentUser,
                                               @RequestBody BudgetDTO budgetRequest,
                                               @PathVariable Long budgetId) {

        Budget updatedBudget = budgetService.updateBudget(budgetId, budgetRequest, currentUser.getId());

        return ResponseEntity.ok(updatedBudget);
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<?> deleteBudget(@AuthenticationPrincipal UserPrincipal currentUser,
                                          @PathVariable Long budgetId
                                          ){
        budgetService.deleteBudget(budgetId, currentUser.getId());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{budgetId}/add-member")
    public ResponseEntity<BudgetMember> addMember(
            @PathVariable Long budgetId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody BudgetMemberDTO budgetMemberRequest) {
        BudgetMember member = budgetMemberService.addMember(
                budgetId,
                budgetMemberRequest,
                currentUser.getId()
        );
        return ResponseEntity.ok(member);
    }


    @PutMapping("/{budgetId}/change-role")
    public ResponseEntity<BudgetMember> changeMemberRole(
            @PathVariable Long budgetId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody BudgetMemberDTO request) {

        BudgetMember member = budgetMemberService.changeMemberRole(budgetId, request.getId(), request.getRole(), currentUser.getId());

        return ResponseEntity.ok(member);
    }


    @DeleteMapping("/{budgetId}/kick-member")
    public ResponseEntity<?> kickMember(
            @PathVariable Long budgetId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody BudgetMemberDTO request) {

            budgetMemberService.removeMember(budgetId, request.getId(), currentUser.getId());
            return ResponseEntity.noContent().build();

    }


}

