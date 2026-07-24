package com.bankapp.exception;

public class ForbiddenOperationException extends RuntimeException implements DomainException {
    public ForbiddenOperationException(String message) { super(message); }
}

