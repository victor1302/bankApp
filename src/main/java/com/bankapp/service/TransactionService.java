package com.bankapp.service;

import com.bankapp.dto.LedgerEntry.Debit.DebitResponseDto;
import com.bankapp.dto.Transaction.CreateTransactionDto;
import com.bankapp.dto.Transaction.CreationTransactionResponseDto;
import com.bankapp.entity.Account;
import com.bankapp.entity.Transaction;
import com.bankapp.entity.User;
import com.bankapp.exception.AccountDontHaveEnoughMoney;
import com.bankapp.exception.AlreadyDisabledOrNotPresent;
import com.bankapp.exception.AlreadyExistsException;
import com.bankapp.exception.UserOrAccountDisabled;
import com.bankapp.interfaces.TransactionProjection;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.TransactionRepository;
import com.bankapp.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

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
    public CreationTransactionResponseDto createDebitTransaction(CreateTransactionDto createTransactionDto){


        Transaction transaction = createAndSaveTransaction(createTransactionDto);
        DebitResponseDto debitResponseDto = ledgerService.createTransferEntries(transaction);


        return new CreationTransactionResponseDto(
                transaction.getTransactionId(),
                debitResponseDto
        );


    }


    public Transaction createAndSaveTransaction(CreateTransactionDto createTransactionDto){
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

        if(!sourceAccount.isActive() || !destinationAccount.isActive()){
            throw new UserOrAccountDisabled("Source or destination account do not exists or disabled");
        }
        if(!sourceUser.isActive()){
            throw new UserOrAccountDisabled("Source user do not exists or disabled");
        }
        Transaction newTransaction = new Transaction();
        newTransaction.setDestinationAccount(destinationAccount);
        newTransaction.setSourceAccount(sourceAccount);
        newTransaction.setAmount(createTransactionDto.amount());

        transactionRepository.save(newTransaction);
        return newTransaction;
    }
    

    @Transactional
    public Page<TransactionProjection> getTransactions(Pageable pageable){
        return transactionRepository.findAllBy(pageable);
    }
}
