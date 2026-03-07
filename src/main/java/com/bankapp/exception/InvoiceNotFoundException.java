package com.bankapp.exception;

public class InvoiceNotFoundException extends RuntimeException implements DomainException {
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
