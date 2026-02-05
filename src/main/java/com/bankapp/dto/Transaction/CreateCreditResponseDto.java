package com.bankapp.dto.Transaction;

import com.bankapp.dto.LedgerEntry.CreditResponseDto;

public record CreateCreditResponseDto(Long transactionId, CreditResponseDto transferData) {
}
