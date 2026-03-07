package com.bankapp.exception;

public class AccountNotFoundException extends RuntimeException implements DomainException {
    public AccountNotFoundException(String message) { super(message); }
}

