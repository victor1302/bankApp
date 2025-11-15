package com.bankapp.dto.Invoice;

import java.math.BigDecimal;

public record CrateInvoiceRequestDto(BigDecimal totalAmount, int installmentCount, String description) {
}
