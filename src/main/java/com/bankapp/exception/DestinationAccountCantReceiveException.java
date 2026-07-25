package com.bankapp.exception;

public class DestinationAccountCantReceiveException extends RuntimeException {
    public DestinationAccountCantReceiveException(String message) {
        super(message);
    }
}
