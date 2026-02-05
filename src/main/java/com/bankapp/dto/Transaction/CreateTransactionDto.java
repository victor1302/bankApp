package com.bankapp.dto.Transaction;


import java.math.BigDecimal;

public record CreateTransactionDto(BigDecimal amount, Long sourceAccountId, Long destinationAccountId) {
}
