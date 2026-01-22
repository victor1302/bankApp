package com.bankapp.dto.LedgerEntry.Debit;

import com.bankapp.entity.enums.EntryStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record DebitResponseDto(
        BigDecimal amount,
        EntryStatus status,
        Instant createdAt
) {}
