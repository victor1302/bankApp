package com.bankapp.service;

import com.bankapp.dto.Invoice.CreateInvoiceRequestDto;
import com.bankapp.dto.Invoice.CreateInvoiceResponseDto;
import com.bankapp.dto.LedgerEntry.CreditResponseDto;
import com.bankapp.dto.LedgerEntry.InvoiceResponseDto;
import com.bankapp.dto.LedgerEntry.PixResonseDto;
import com.bankapp.dto.Transaction.*;
import com.bankapp.entity.*;
import com.bankapp.entity.enums.InvoiceStatus;
import com.bankapp.entity.enums.TransactionStatus;
import com.bankapp.entity.enums.TransactionType;
import com.bankapp.exception.AccountDontHaveEnoughMoney;
import com.bankapp.exception.UserOrAccountDisabled;
import com.bankapp.interfaces.TransactionProjection;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.InvoiceRepository;
import com.bankapp.repository.TransactionRepository;
import com.bankapp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final LedgerService ledgerService;
    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, AccountRepository accountRepository, LedgerService ledgerService, InvoiceService invoiceService, InvoiceRepository invoiceRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.ledgerService = ledgerService;
        this.invoiceService = invoiceService;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public CreateTransactionResponseDto createPixTransaction(CreateTransactionDto createTransactionDto){
        Transaction transaction = createAndVerifyTransaction(createTransactionDto.transactionType(),
                createTransactionDto.amount(),
                createTransactionDto.destinationAccountId());

        PixResonseDto transferStatus = ledgerService.createPixLedger(transaction);
        updateCachedBalance(transaction);

        return new CreateTransactionResponseDto(
                transaction.getTransactionId(),
                transferStatus
        );
    }

    public Transaction createAndVerifyTransaction(TransactionType transactionType,
                                                  BigDecimal amount,
                                                  Long destinationAccountIdOrNull){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User sourceUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Account sourceAccount = accountRepository.findById(sourceUser.getUserAccount().getAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found!"));

        Account destinationAccount = null;
        if(destinationAccountIdOrNull != null){
            destinationAccount = accountRepository.findById(destinationAccountIdOrNull)
                    .orElseThrow(() -> new RuntimeException("Destination account not found"));
        }

        validateByType(transactionType, amount, sourceAccount, destinationAccount, sourceUser);
        Transaction newTransaction = new Transaction();
        newTransaction.setDestinationAccount(destinationAccount);
        newTransaction.setSourceAccount(sourceAccount);
        newTransaction.setStatus(TransactionStatus.COMPLETED);
        newTransaction.setAmount(amount);
        newTransaction.setTransactionType(transactionType);

        return transactionRepository.save(newTransaction);

    }

    private void validateByType(TransactionType transactionType,
                                BigDecimal amount,
                                Account sourceAccount,
                                Account destinationAccount,
                                User sourceUser) {
        if (!sourceUser.isActive()) {
            throw new UserOrAccountDisabled("Source user do not exists or disabled");
        }
        if (!sourceAccount.isActive()) {
            throw new UserOrAccountDisabled("Source account do not exists or disabled");
        }

        switch (transactionType) {
            case PIX_TRANSFER -> {
                if (destinationAccount == null) {
                    throw new RuntimeException("Destination account is required for PIX_TRANSFER");
                }
                if (!destinationAccount.isActive()) {
                    throw new UserOrAccountDisabled("Destination account do not exists or disabled");
                }
                if (sourceAccount.getAccountId().equals(destinationAccount.getAccountId())) {
                    throw new RuntimeException("You can't send money to yourself");
                }
                if (sourceAccount.getCachedBalance().compareTo(amount) < 0) {
                    throw new AccountDontHaveEnoughMoney("Account don't have enough money!");
                }
            }
            case CREDIT_PURCHASE -> {
                if (destinationAccount == null) {
                    throw new RuntimeException("Destination account is required for CREDIT_PURCHASE");
                }
                if (!destinationAccount.isActive()) {
                    throw new UserOrAccountDisabled("Destination account do not exists or disabled");
                }
            }
            case INVOICE_PAYMENT -> {
                if (destinationAccount != null) {
                    throw new RuntimeException(transactionType + " must not have destination account");
                }
                if (sourceAccount.getCachedBalance().compareTo(amount) < 0) {
                    throw new AccountDontHaveEnoughMoney("Account don't have enough money!");
                }
            }
            case DEPOSIT -> {
                if (destinationAccount == null) {
                    throw new RuntimeException("Destination account is required for DEPOSIT");
                }
                if (!destinationAccount.isActive()) {
                    throw new UserOrAccountDisabled("Destination account do not exists or disabled");
                }
            }

        }
    }

    private void updateCachedBalance(Transaction transaction) {
        Account sourceAccount = transaction.getSourceAccount();
        Account destinationAccount = transaction.getDestinationAccount();
        BigDecimal amount = transaction.getAmount();

        switch (transaction.getTransactionType()) {
            case PIX_TRANSFER -> {
                sourceAccount.setCachedBalance(sourceAccount.getCachedBalance().subtract(amount));
                destinationAccount.setCachedBalance(destinationAccount.getCachedBalance().add(amount));
                accountRepository.save(sourceAccount);
                accountRepository.save(destinationAccount);
            }
            case CREDIT_PURCHASE -> {
                destinationAccount.setCachedBalance(destinationAccount.getCachedBalance().add(amount));
                accountRepository.save(destinationAccount);
            }
            case INVOICE_PAYMENT -> {
                sourceAccount.setCachedBalance(sourceAccount.getCachedBalance().subtract(amount));
                accountRepository.save(sourceAccount);
            }
            case DEPOSIT -> {
                destinationAccount.setCachedBalance(destinationAccount.getCachedBalance().add(amount));
                accountRepository.save(destinationAccount);
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<TransactionProjection> getTransactions(Pageable pageable){
        return transactionRepository.findAllBy(pageable);
    }
}
