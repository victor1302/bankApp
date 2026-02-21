package com.bankapp.dto.Transaction;

import com.bankapp.dto.Invoice.CreateInvoiceResponseDto;
import com.bankapp.dto.LedgerEntry.CreditResponseDto;

public record CreateCreditResponse(CreditResponseDto transactionStatus, CreateInvoiceResponseDto invoiceStatus, GetTransactionAccount sourceAccount, GetTransactionAccount destinationAccount) {
}
