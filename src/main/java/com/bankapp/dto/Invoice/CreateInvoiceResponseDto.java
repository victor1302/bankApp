package com.bankapp.dto.Invoice;

import java.math.BigDecimal;

public record CreateInvoiceResponseDto(BigDecimal amount, int installmentCount, String description) {
}
