package com.bankapp.dto.Installments;

import com.bankapp.entity.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record LedgerInstallmentResponse(PayInstallmentResponseDto transferStatus) {
}
