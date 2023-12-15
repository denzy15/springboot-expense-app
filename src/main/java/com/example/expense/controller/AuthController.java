package com.example.expense.controller;

import com.example.expense.DTO.LoginRequest;
import com.example.expense.DTO.LoginResponse;
import com.example.expense.DTO.RegistrationUserDto;
import com.example.expense.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.createAuthToken(request));
    }

    @PostMapping("/registration")
    public ResponseEntity<?> register(@RequestBody RegistrationUserDto registrationUserDto) throws Exception {
        authService.registerUser(registrationUserDto);
        return ResponseEntity.created(null).build();
    }


}