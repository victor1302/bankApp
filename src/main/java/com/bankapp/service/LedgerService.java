package com.bankapp.service;

import com.bankapp.dto.Installments.LedgerInstallmentResponse;
import com.bankapp.dto.Installments.PayInstallmentResponseDto;
import com.bankapp.dto.LedgerEntry.CreditResponseDto;
import com.bankapp.dto.LedgerEntry.InvoiceResponseDto;
import com.bankapp.dto.LedgerEntry.PixResonseDto;
import com.bankapp.entity.*;
import com.bankapp.entity.enums.EntryStatus;
import com.bankapp.entity.enums.EntryType;
import com.bankapp.entity.enums.ReferenceType;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.LedgerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class LedgerService {
    private final LedgerRepository ledgerRepository;

    public LedgerService(LedgerRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }

    @Transactional
    public PixResonseDto createPixLedger(Transaction transaction) {
        createLedgerEntry(
                transaction.getSourceAccount().getAccountId(),
                transaction.getAmount(),
                EntryType.DEBIT,
                ReferenceType.TRANSFER,
                transaction.getTransactionId(),
                "Pix"
        );
        createLedgerEntry(
                transaction.getDestinationAccount().getAccountId(),
                transaction.getAmount(),
                EntryType.CREDIT,
                ReferenceType.TRANSFER,
                transaction.getTransactionId(),
                "Pix"
        );
        return new PixResonseDto(
                transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getCreationTimestamp()
        );
    }

    @Transactional
    public CreditResponseDto createCreditLedger(Transaction transaction){
        createLedgerEntry(
                transaction.getSourceAccount().getAccountId(),
                transaction.getAmount(),
                EntryType.DEBIT,
                ReferenceType.CARD_PURCHASE,
                transaction.getTransactionId(),
                "Credit card purchase"
        );
        createLedgerEntry(
                transaction.getDestinationAccount().getAccountId(),
                transaction.getAmount(),
                EntryType.CREDIT,
                ReferenceType.CARD_PURCHASE,
                transaction.getTransactionId(),
                "Credit card receivable"
        );
        return new CreditResponseDto(
                transaction.getTransactionId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getCreationTimestamp()
        );
    }

    @Transactional
    public InvoiceResponseDto createInvoiceLedger(Transaction transaction){
        createLedgerEntry(
                transaction.getSourceAccount().getAccountId(),
                transaction.getAmount(),
                EntryType.DEBIT,
                ReferenceType.INVOICE,
                transaction.getTransactionId(),
                "Paying invoice"
        );
        return new InvoiceResponseDto(
                transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getCreationTimestamp()
        );
    }

    @Transactional
    public PayInstallmentResponseDto createInstallmentLedger(Transaction transaction){
        createLedgerEntry(
                transaction.getSourceAccount().getAccountId(),
                transaction.getAmount(),
                EntryType.DEBIT,
                ReferenceType.INSTALLMENT,
                transaction.getTransactionId(),
                "Paying installment"
        );
        return new PayInstallmentResponseDto(
                transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getCreationTimestamp()
        );
    }

    private LedgerEntry createLedgerEntry(Long accountId, BigDecimal amount, EntryType entryType, ReferenceType referenceType, Long referenceId, String description){
        LedgerEntry entry = new LedgerEntry();
        entry.setAccountId(accountId);
        entry.setAmount(entryType.apply(amount));
        entry.setEntryType(entryType);
        entry.setReferenceType(referenceType);
        entry.setReferenceId(referenceId);
        entry.setDescription(description);
        entry.setEntryStatus(EntryStatus.COMPLETED);
        return ledgerRepository.saveAndFlush(entry);
    }


}
