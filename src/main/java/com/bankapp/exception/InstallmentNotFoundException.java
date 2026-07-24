package com.bankapp.exception;

public class InstallmentNotFoundException extends RuntimeException implements DomainException {
    public InstallmentNotFoundException(String message) { super(message); }
}

