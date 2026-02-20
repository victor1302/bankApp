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
        Transaction transaction = createAndVerifyTransaction(createTransactionDto);
        PixResonseDto transferStatus = ledgerService.createPixLedger(transaction);
        updateCachedBalance(transaction);

        return new CreateTransactionResponseDto(
                transaction.getTransactionId(),
                transferStatus
        );
    }

    @Transactional
    public CreateCreditResponseDto createCreditTransaction(CreateTransactionDto createTransactionDto){
        Transaction transaction = createAndVerifyTransaction(createTransactionDto);
        CreditResponseDto transferStatus = ledgerService.createCreditLedger(transaction);
        createInvoiceForCreditTransaction(transaction, createTransactionDto);
        updateCachedBalanceForCreditTransactions(transaction);

        return new CreateCreditResponseDto(
                transaction.getTransactionId(),
                transferStatus
        );

    }

    @Transactional
    public PayInvoiceResponse payInvoice(PayInvoiceRequest payInvoiceRequest){
        Transaction transaction = createAndVerifyTransactionToPayInvoice(payInvoiceRequest);
        invoiceService.payInvoice(payInvoiceRequest.invoiceId(), payInvoiceRequest.amount());
        InvoiceResponseDto transferStatus = ledgerService.createInvoiceLedger(transaction);
        updateCachedBalanceForPayInvoice(transaction);
        return new PayInvoiceResponse(
                payInvoiceRequest.amount(),
                transferStatus
        );
    }

    @Transactional
    protected CreateInvoiceResponseDto createInvoiceForCreditTransaction(Transaction transaction,
                                                                         CreateTransactionDto createTransactionDto) {
        Account sourceAccount = transaction.getSourceAccount();
        Account destinationAccount = transaction.getDestinationAccount();

        Long sourceAccountId = sourceAccount.getAccountId();
        Long destinationAccountId = destinationAccount.getAccountId();
        BigDecimal totalAmount = transaction.getAmount();

        int installmentCount = createTransactionDto.totalInstallments();
        String description = createTransactionDto.description();

        CreateInvoiceRequestDto invoiceRequest = new CreateInvoiceRequestDto(
                sourceAccountId,
                destinationAccountId,
                totalAmount,
                installmentCount,
                description
        );

        return invoiceService.createInvoice(invoiceRequest);
    }
    public Transaction createAndVerifyTransaction(CreateTransactionDto createTransactionDto){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User sourceUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Account sourceAccount = accountRepository.findById(sourceUser.getUserAccount().getAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found!"));

        Account destinationAccount = accountRepository.findById(createTransactionDto.destinationAccountId())
                .orElseThrow(() -> new RuntimeException("Destination account not found!"));

        if(sourceAccount.getCachedBalance().compareTo(createTransactionDto.amount()) < 0){
            throw new AccountDontHaveEnoughMoney("Account don't have enough money!");
        }
        if(createTransactionDto.amount().equals(BigDecimal.ZERO)){
            throw new RuntimeException("You cant send $0");
        }
        if(sourceAccount == destinationAccount){
            throw new RuntimeException("You can't send money to yourself");
        }

        if(!sourceAccount.isActive() || !destinationAccount.isActive()){
            throw new UserOrAccountDisabled("Source or destination account do not exists or disabled");
        }
        if(!sourceUser.isActive()){
            throw new UserOrAccountDisabled("Source user do not exists or disabled");
        }
        Transaction newTransaction = new Transaction();
        newTransaction.setDestinationAccount(destinationAccount);
        newTransaction.setSourceAccount(sourceAccount);
        newTransaction.setStatus(TransactionStatus.COMPLETED);
        newTransaction.setAmount(createTransactionDto.amount());

        transactionRepository.save(newTransaction);
        return newTransaction;
    }

    public Transaction createAndVerifyTransactionToPayInvoice(PayInvoiceRequest payInvoiceRequest){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account sourceAccount = accountRepository.findById(user.getUserAccount().getAccountId()).
                orElseThrow( () -> new RuntimeException("Not found source account"));

        Invoice invoice = invoiceRepository.findById(payInvoiceRequest.invoiceId())
                .orElseThrow(() -> new RuntimeException("Not found the invoice"));

        if(!invoice.getCreditCard().getCardAccount().getAccountId().equals(sourceAccount.getAccountId())){
            throw new RuntimeException("This invoice does not belong to you");
        }
        if(!invoice.getStatus().equals(InvoiceStatus.OPEN)){
            throw new RuntimeException("You cant pay this invoice");
        }

        BigDecimal remaining = invoice.getTotalAmount().subtract(invoice.getAmountPaid());
        if(payInvoiceRequest.amount().compareTo(BigDecimal.ZERO) <= 0 || payInvoiceRequest.amount().compareTo(remaining) > 0){
            throw new RuntimeException("Invalid payment amount");
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setSourceAccount(sourceAccount);
        newTransaction.setStatus(TransactionStatus.COMPLETED);
        newTransaction.setAmount(payInvoiceRequest.amount());
        transactionRepository.save(newTransaction);
        return newTransaction;
    }


    private void updateCachedBalance(Transaction transaction) {
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
    private void updateCachedBalanceForCreditTransactions(Transaction transaction){
        Account destinationAccount = transaction.getDestinationAccount();
        destinationAccount.setCachedBalance(
                destinationAccount.getCachedBalance().add(transaction.getAmount())
        );
        accountRepository.save(destinationAccount);
    }
    private void updateCachedBalanceForPayInvoice(Transaction transaction){
        Account sourceAccount = transaction.getSourceAccount();
        sourceAccount.setCachedBalance(
                sourceAccount.getCachedBalance().subtract(transaction.getAmount())
        );
        accountRepository.save(sourceAccount);
    }

    @Transactional
    public Page<TransactionProjection> getTransactions(Pageable pageable){
        return transactionRepository.findAllBy(pageable);
    }
}
