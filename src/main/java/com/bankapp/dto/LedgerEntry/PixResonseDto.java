package com.bankapp.dto.LedgerEntry;

import com.bankapp.entity.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PixResonseDto(Long id, BigDecimal value, TransactionStatus transactionStatus, Instant createdAt) {
}
