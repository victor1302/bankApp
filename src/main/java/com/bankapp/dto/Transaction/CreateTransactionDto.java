package com.bankapp.dto.Transaction;


import com.bankapp.entity.enums.TransactionType;

import java.math.BigDecimal;

public record CreateTransactionDto(TransactionType transactionType, BigDecimal amount, Long destinationAccountId) {
}
