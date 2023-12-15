package com.example.expense.service;

import com.example.expense.DTO.LoginRequest;
import com.example.expense.DTO.LoginResponse;
import com.example.expense.DTO.RegistrationUserDto;
import com.example.expense.exceptions.UserAlreadyExistAuthenticationException;
import com.example.expense.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public LoginResponse createAuthToken(LoginRequest authRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getEmail(),
                    authRequest.getPassword()
            ));
        } catch (BadCredentialsException e){
            throw new BadCredentialsException("Wrong email or password");
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getEmail());
        String token = jwtTokenUtils.generateToken(userDetails);
        return new LoginResponse(userDetails.getUsername(), token);

    }


    public void registerUser(RegistrationUserDto registrationUserDto) throws Exception {
        if (userService.getUserByEmail(registrationUserDto.getEmail()) != null){
            throw new UserAlreadyExistAuthenticationException("User with this email already exists");
        }

        userService.createUser(registrationUserDto);

    }





}
