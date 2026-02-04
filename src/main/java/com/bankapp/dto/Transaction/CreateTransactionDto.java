package com.bankapp.dto.Transaction;

import com.bankapp.entity.enums.TransactionStatus;

import java.math.BigDecimal;

public record CreateTransactionDto(BigDecimal amount, Long sourceAccountId, Long destinationAccountId, TransactionStatus transactionStatus) {
}
