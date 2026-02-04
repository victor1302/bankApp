package com.bankapp.dto.Transaction.Card;

import com.bankapp.entity.enums.EntryStatus;

import java.math.BigDecimal;

public record CreateCreditTransactionResponse(BigDecimal amount) {
}
