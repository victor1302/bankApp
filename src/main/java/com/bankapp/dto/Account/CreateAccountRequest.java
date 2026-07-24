package com.bankapp.dto.Account;

import com.bankapp.entity.enums.AccountType;

public record CreateAccountRequest(AccountType accountType) {
}
