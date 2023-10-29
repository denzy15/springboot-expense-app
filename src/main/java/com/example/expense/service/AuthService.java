package com.example.expense.service;

import com.example.expense.DTO.JwtRequest;
import com.example.expense.DTO.RegistrationUserDto;
import com.example.expense.DTO.UserDto;
import com.example.expense.exceptions.AppError;
import com.example.expense.model.User;
import com.example.expense.repository.UserRepository;
import com.example.expense.utils.JwtTokenUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public String createAuthToken(JwtRequest authRequest){

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getEmail(),
                    authRequest.getPassword()
            ));
        } catch (BadCredentialsException e){
            throw new BadCredentialsException("Wrong email or password");
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getEmail());
        return jwtTokenUtils.generateToken(userDetails);

    }


    public UserDto registerUser(RegistrationUserDto registrationUserDto) throws Exception {
        if (userService.getUserByEmail(registrationUserDto.getEmail()) != null){
            throw new Exception("User with this email already exists");
        }

        User created = userService.createUser(registrationUserDto);
        return new UserDto(created.getId(), created.getUsername(), created.getEmail());
    }


}
