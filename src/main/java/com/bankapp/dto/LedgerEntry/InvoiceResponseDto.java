package com.bankapp.dto.LedgerEntry;

import com.bankapp.entity.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record InvoiceResponseDto(Long id, BigDecimal amount, TransactionStatus transactionStatus, Instant createdAt) {
}
