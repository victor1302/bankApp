package com.bankapp.dto.Transaction;

import com.bankapp.dto.LedgerEntry.InvoiceResponseDto;


public record PayInvoiceResponse(InvoiceResponseDto transferStatus) {
}
