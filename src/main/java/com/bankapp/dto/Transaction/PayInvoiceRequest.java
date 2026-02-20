package com.bankapp.dto.Transaction;

import java.math.BigDecimal;

public record PayInvoiceRequest(Long invoiceId, BigDecimal amount) {
}
