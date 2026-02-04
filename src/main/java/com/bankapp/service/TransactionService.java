package com.bankapp.service;

import com.bankapp.dto.LedgerEntry.CreditResponseDto;
import com.bankapp.dto.LedgerEntry.PixResonseDto;
import com.bankapp.dto.Transaction.CreateCreditResponseDto;
import com.bankapp.dto.Transaction.CreateTransactionDto;
import com.bankapp.dto.Transaction.CreateTransactionResponseDto;
import com.bankapp.entity.*;
import com.bankapp.entity.enums.TransactionStatus;
import com.bankapp.exception.AccountDontHaveEnoughMoney;
import com.bankapp.exception.UserOrAccountDisabled;
import com.bankapp.interfaces.TransactionProjection;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.TransactionRepository;
import com.bankapp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final LedgerService ledgerService;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, AccountRepository accountRepository, LedgerService ledgerService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.ledgerService = ledgerService;
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

        //adicionar as invoices aqui!!!

        return new CreateCreditResponseDto(
                transaction.getTransactionId(),
                transferStatus
        );

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
            throw new AccountDontHaveEnoughMoney("Account dont have enough money!");
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

    @Transactional
    public Page<TransactionProjection> getTransactions(Pageable pageable){
        return transactionRepository.findAllBy(pageable);
    }
}
