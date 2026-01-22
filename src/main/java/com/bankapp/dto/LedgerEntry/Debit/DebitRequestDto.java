package com.bankapp.dto.LedgerEntry.Debit;

import com.bankapp.entity.enums.ReferenceType;

import java.math.BigDecimal;


public record DebitRequestDto(Long transactionId, String description){
}
