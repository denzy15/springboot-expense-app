package com.example.expense.controller;

import com.example.expense.DTO.JwtRequest;
import com.example.expense.DTO.JwtResponse;
import com.example.expense.DTO.RegistrationUserDto;
import com.example.expense.DTO.UserDto;
import com.example.expense.exceptions.AppError;
import com.example.expense.service.AuthService;
import com.example.expense.service.UserService;
import com.example.expense.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private  AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest request) {
        String token;
        try {

        token = authService.createAuthToken(request);
        } catch (Exception e){
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
        // JWTResponse may be unnecessary
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/registration")
    public ResponseEntity<?> register(@RequestBody RegistrationUserDto registrationUserDto) {
        UserDto newUser;
        try {
            newUser = authService.registerUser(registrationUserDto);
        } catch (Exception e) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(new UserDto(
                newUser.getId(),
                newUser.getUsername(),
                newUser.getEmail())
        );
    }


}