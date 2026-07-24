package com.bankapp.exception;

public class SelfTransferNotAllowedException extends RuntimeException implements DomainException {
    public SelfTransferNotAllowedException(String message) { super(message); }
}

