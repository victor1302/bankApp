package com.bankapp.dto.Transaction;


import java.math.BigDecimal;

public record CreateCreditRequest(BigDecimal amount, Long destinationAccountId, int totalInstallments, String description) {
}
