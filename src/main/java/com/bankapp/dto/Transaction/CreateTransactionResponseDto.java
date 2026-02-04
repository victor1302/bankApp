package com.bankapp.dto.Transaction;


import com.bankapp.dto.LedgerEntry.PixResonseDto;

public record CreateTransactionResponseDto(Long transactionId, PixResonseDto transferResonseDto) {
}
