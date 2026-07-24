package com.bankapp.exception;

public class UserNotFoundException extends RuntimeException implements DomainException {
    public UserNotFoundException(String message) { super(message); }
}

