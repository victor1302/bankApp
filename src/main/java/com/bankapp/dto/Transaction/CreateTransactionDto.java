package com.bankapp.dto.Transaction;


import java.math.BigDecimal;

public record CreateTransactionDto(Long destinationAccountId, BigDecimal amount) {
}
