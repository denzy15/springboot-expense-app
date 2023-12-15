package com.example.expense.service;

import com.example.expense.DTO.SavingRequest;
import com.example.expense.model.Saving;
import com.example.expense.model.User;
import com.example.expense.repository.SavingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingService {
    @Autowired
    private final SavingRepository savingRepository;
    @Autowired
    private final UserService userService;

    public List<Saving> getAllUserSavings() {
        User authUser = userService.getAuthorizedUser();
        return savingRepository.findByUserId(authUser.getId());
    }

    public Saving createSaving(SavingRequest savingRequest) throws AccessDeniedException {
        User authUser = userService.getAuthorizedUser();

        Saving newSaving = new Saving();
        newSaving.setUser(authUser);
        newSaving.setName(savingRequest.getName());
        newSaving.setAmount(savingRequest.getAmount());

        return savingRepository.save(newSaving);
    }

    public Saving updateSaving(Long savingId, SavingRequest savingRequest) throws AccessDeniedException {
        User authUser = userService.getAuthorizedUser();

        Saving existingSaving = savingRepository.findById(savingId)
                .orElseThrow(() -> new EntityNotFoundException("Saving with id " + savingId + " not found"));

        if (!existingSaving.getUser().equals(authUser)){
            throw new AccessDeniedException("You do not have access to modify this data");
        }

        existingSaving.setName(savingRequest.getName());
        existingSaving.setAmount(savingRequest.getAmount());

        return savingRepository.save(existingSaving);
    }

    public void deleteSaving(Long savingId) throws AccessDeniedException {
        User authUser = userService.getAuthorizedUser();

        Saving existingSaving = savingRepository.findById(savingId)
                .orElseThrow(() -> new EntityNotFoundException("Saving with id " + savingId + " not found"));

        if (!existingSaving.getUser().equals(authUser)){
            throw new AccessDeniedException("You do not have access to modify this data");
        }

        savingRepository.delete(existingSaving);

    }

}
