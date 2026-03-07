package com.bankapp.exception;

/**
 * Generic business-rule violation (e.g. paying wrong invoice, wrong card, etc.).
 */
public class BusinessValidationException extends RuntimeException implements DomainException {
    public BusinessValidationException(String message) { super(message); }
}

