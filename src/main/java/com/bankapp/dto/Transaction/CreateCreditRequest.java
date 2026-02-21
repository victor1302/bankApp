package com.bankapp.dto.Transaction;

import com.bankapp.dto.LedgerEntry.CreditResponseDto;

import java.math.BigDecimal;

public record CreateCreditRequest(BigDecimal amount, Long destinationAccountId, int totalInstallments, String description) {
}
