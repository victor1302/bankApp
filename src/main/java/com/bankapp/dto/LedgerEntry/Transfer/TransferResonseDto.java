package com.bankapp.dto.LedgerEntry.Transfer;

import com.bankapp.entity.enums.EntryStatus;
import com.bankapp.entity.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferResonseDto(Long id, BigDecimal value, TransactionStatus transactionStatus, Instant createdAt) {
}
