package com.bankapp.service;

import com.bankapp.dto.LedgerEntry.CreditResponseDto;
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
    private final AccountRepository accountRepository;

    public LedgerService(LedgerRepository ledgerRepository, AccountRepository accountRepository) {
        this.ledgerRepository = ledgerRepository;
        this.accountRepository = accountRepository;
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
                "Credit card receivable"
        );
        createLedgerEntry(
                transaction.getDestinationAccount().getAccountId(),
                transaction.getAmount(),
                EntryType.CREDIT,
                ReferenceType.CARD_PURCHASE,
                transaction.getTransactionId(),
                "Credit card purchase"
        );
        return new CreditResponseDto(
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
