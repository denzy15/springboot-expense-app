package com.example.expense.controller;

import com.example.expense.DTO.SavingRequest;
import com.example.expense.DTO.SavingResponse;
import com.example.expense.model.Saving;
import com.example.expense.service.SavingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/savings")
@RequiredArgsConstructor
public class SavingController {
    @Autowired
    private final SavingService savingService;

    @GetMapping
    public ResponseEntity<?> getAllUserSavings() {
        List<Saving> savings = savingService.getAllUserSavings();
        return ResponseEntity.ok(SavingResponse.convertMany(savings));
    }

    @PostMapping
    public ResponseEntity<?> createSaving(@RequestBody @Valid SavingRequest savingRequest) {
        try {
            Saving createdSaving = savingService.createSaving(savingRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(SavingResponse.convertOne(createdSaving));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{savingId}")
    public ResponseEntity<?> updateSaving(@PathVariable Long savingId,
                                          @RequestBody @Valid SavingRequest savingRequest) {
        try {
            Saving updatedSaving = savingService.updateSaving(savingId, savingRequest);
            return ResponseEntity.ok(SavingResponse.convertOne(updatedSaving));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/{savingId}")
    public ResponseEntity<?> deleteSaving(@PathVariable Long savingId) {
        try {
            savingService.deleteSaving(savingId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }

    }

}
