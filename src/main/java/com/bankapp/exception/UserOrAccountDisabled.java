package com.bankapp.exception;

public class UserOrAccountDisabled extends RuntimeException {
    public UserOrAccountDisabled(String message) {
        super(message);
    }
}
