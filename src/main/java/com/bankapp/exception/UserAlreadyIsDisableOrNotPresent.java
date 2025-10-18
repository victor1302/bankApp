package com.bankapp.exception;

public class UserAlreadyIsDisableOrNotPresent extends RuntimeException {
    public UserAlreadyIsDisableOrNotPresent(String message) {
        super(message);
    }
}
