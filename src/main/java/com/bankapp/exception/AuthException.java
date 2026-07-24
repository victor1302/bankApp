package com.bankapp.exception;

public class AuthException extends RuntimeException implements DomainException {
    public AuthException(String message) {
        super(message);
    }
}
