package com.bankapp.exception;

public class AccountDontHaveEnoughMoney extends RuntimeException {
    public AccountDontHaveEnoughMoney(String message) {
        super(message);
    }
}
