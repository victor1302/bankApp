package com.bankapp.dto.Transaction;

import java.math.BigDecimal;

public record CreationTransactionResponseDto(String sourceUsername, int sourceAccountNumber, String destinationUsername, int destinationAccountNumber,
                                             BigDecimal amount, Long transactionId) {
}
