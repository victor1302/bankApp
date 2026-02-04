package com.bankapp.dto.Transaction;


import com.bankapp.dto.LedgerEntry.Transfer.TransferResonseDto;

public record CreateTransactionResponseDto(Long transactionId, TransferResonseDto transferResonseDto) {
}
