package com.bankapp.dto.Transaction;


import com.bankapp.dto.LedgerEntry.Debit.DebitResponseDto;

public record CreationTransactionResponseDto(Long transactionId, DebitResponseDto debitResponseDto) {
}
