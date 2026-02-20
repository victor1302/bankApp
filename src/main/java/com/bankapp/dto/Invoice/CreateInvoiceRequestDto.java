package com.bankapp.dto.Invoice;

import java.math.BigDecimal;

public record CreateInvoiceRequestDto(Long sourceAccount, Long destinationAccount, BigDecimal totalAmount, int installmentCount, String description) {
}
