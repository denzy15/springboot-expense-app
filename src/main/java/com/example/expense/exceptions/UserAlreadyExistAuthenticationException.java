package com.example.expense.exceptions;

public class UserAlreadyExistAuthenticationException extends Exception {
    public UserAlreadyExistAuthenticationException(final String msg) {
        super(msg);
    }

}