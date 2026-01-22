package com.bankapp.dto.Transaction;


import com.bankapp.entity.Account;

import java.math.BigDecimal;

public record CreateTransactionDto(BigDecimal amount, Long destinationAccountId) {
}
