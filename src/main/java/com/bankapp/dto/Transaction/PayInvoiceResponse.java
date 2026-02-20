package com.bankapp.dto.Transaction;

import com.bankapp.dto.LedgerEntry.InvoiceResponseDto;

import java.math.BigDecimal;
import java.time.Instant;

public record PayInvoiceResponse(BigDecimal amount, InvoiceResponseDto transferStatus) {
}
