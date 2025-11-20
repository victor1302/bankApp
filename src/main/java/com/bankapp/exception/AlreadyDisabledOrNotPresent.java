package com.bankapp.exception;

public class AlreadyDisabledOrNotPresent extends RuntimeException {
    public AlreadyDisabledOrNotPresent(String message) {
        super(message);
    }
}
