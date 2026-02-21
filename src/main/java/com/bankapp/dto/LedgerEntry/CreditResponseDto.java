package com.bankapp.dto.LedgerEntry;

import com.bankapp.entity.enums.TransactionStatus;
import com.bankapp.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record CreditResponseDto(Long id, TransactionType transactionType, BigDecimal amount, TransactionStatus transactionStatus, Instant createdAt) {
}
