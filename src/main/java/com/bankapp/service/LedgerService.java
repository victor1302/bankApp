package com.bankapp.service;

import com.bankapp.dto.LedgerEntry.Debit.DebitRequestDto;
import com.bankapp.dto.LedgerEntry.Debit.DebitResponseDto;
import com.bankapp.entity.Account;
import com.bankapp.entity.LedgerEntry;
import com.bankapp.entity.Transaction;
import com.bankapp.entity.User;
import com.bankapp.entity.enums.EntryStatus;
import com.bankapp.entity.enums.EntryType;
import com.bankapp.entity.enums.ReferenceType;
import com.bankapp.exception.UserOrAccountDisabled;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.LedgerRepository;
import com.bankapp.repository.TransactionRepository;
import com.bankapp.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class LedgerService {
    private final LedgerRepository ledgerRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public LedgerService(LedgerRepository ledgerRepository, TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.ledgerRepository = ledgerRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public DebitResponseDto createTransferEntries(Transaction transaction){
        LedgerEntry debit =  createDebitTransaction(transaction);
        createCreditTransaction(transaction);
        updateCachedBalance(transaction);

        return new DebitResponseDto(
                debit.getAmount(),
                debit.getEntryStatus(),
                debit.getCreatedAt()
        );
    }

    private LedgerEntry createDebitTransaction(Transaction transaction){
        //Catching users and accounts
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Creating a Ledger
        LedgerEntry debit = new LedgerEntry();
        debit.setAccountId(transaction.getSourceAccount().getAccountId());
        debit.setAmount(transaction.getAmount().negate());
        debit.setEntryType(EntryType.DEBIT);
        debit.setReferenceType(ReferenceType.TRANSFER);
        debit.setReferenceId(transaction.getTransactionId());
        debit.setDescription("PIX");
        debit.setEntryStatus(EntryStatus.COMPLETED);
        ledgerRepository.save(debit);

        return debit;

    }
    private LedgerEntry createCreditTransaction(Transaction transaction){
        //Catching users and accounts

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //Creating a Ledger
        LedgerEntry credit = new LedgerEntry();
        credit.setAccountId(transaction.getDestinationAccount().getAccountId());
        credit.setAmount(transaction.getAmount());
        credit.setEntryType(EntryType.CREDIT);
        credit.setReferenceType(ReferenceType.TRANSFER);
        credit.setReferenceId(transaction.getTransactionId());
        credit.setDescription("PIX");
        credit.setEntryStatus(EntryStatus.COMPLETED);
        ledgerRepository.saveAndFlush(credit);
        return credit;
    }

    private void updateCachedBalance(Transaction transaction){
        Account sourceAccount = transaction.getSourceAccount();
        Account destinationAccount = transaction.getDestinationAccount();

        sourceAccount.setCachedBalance(
                sourceAccount.getCachedBalance().subtract(transaction.getAmount())
        );
        destinationAccount.setCachedBalance(
                destinationAccount.getCachedBalance().add(transaction.getAmount())
        );
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
    }
}
