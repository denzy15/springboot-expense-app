package com.example.expense.controller;

import com.example.expense.DTO.MigrationBudgetAccessDTO;
import com.example.expense.DTO.MigrationCategoryDTO;
import com.example.expense.DTO.MigrationTransDTO;
import com.example.expense.model.Category;
import com.example.expense.model.Transaction;
import com.example.expense.service.MigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/migrate")
@RequiredArgsConstructor
public class MigrationController {


    private final MigrationService migrationService;
    @Value("${migration.secret.token}")
    private String migrationToken;


    @GetMapping("/analytics-trans")
    public ResponseEntity<List<MigrationTransDTO>> migrateTransactions(
            @RequestHeader("X-MIGRATION-TOKEN") String token
    ) {

        if (!migrationToken.equals(token)) {
            throw new AccessDeniedException("Invalid migration token");
        }

        List<MigrationTransDTO> transactions = migrationService.getAllTransactions();

        return ResponseEntity.ok(transactions);

    }

    @GetMapping("/analytics-categories")
    public ResponseEntity<List<MigrationCategoryDTO>> migrateCategories(
            @RequestHeader("X-MIGRATION-TOKEN") String token
    ) {
        if (!migrationToken.equals(token)) {
            throw new AccessDeniedException("Invalid migration token");
        }

        List<MigrationCategoryDTO> categories = migrationService.getAllCategories();

        return ResponseEntity.ok(categories);

    }


    @GetMapping("/analytics-access")
    public ResponseEntity<List<MigrationBudgetAccessDTO>> migrateBudgetAccesses(
            @RequestHeader("X-MIGRATION-TOKEN") String token
    ) {
        if (!migrationToken.equals(token)) {
            throw new AccessDeniedException("Invalid migration token");
        }

        List<MigrationBudgetAccessDTO> accesses = migrationService.getAllAccessData();

        return ResponseEntity.ok(accesses);
    }


}
