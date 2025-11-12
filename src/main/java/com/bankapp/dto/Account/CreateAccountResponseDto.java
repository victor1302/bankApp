package com.bankapp.dto.Account;

import java.math.BigDecimal;

public record CreateAccountResponseDto(int accountNumber, BigDecimal balance) {
}
