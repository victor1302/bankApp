package com.bankapp.exception;

public class DestinationAccountRequiredException extends RuntimeException implements DomainException {
    public DestinationAccountRequiredException(String message) { super(message); }
}

