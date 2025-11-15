package com.bankapp.service;

import com.bankapp.dto.Transaction.CreationTransactionResponseDto;
import com.bankapp.entity.Account;
import com.bankapp.entity.Transaction;
import com.bankapp.entity.User;
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

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public CreationTransactionResponseDto createTransaction(Long destinationAccountId, BigDecimal amount){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User sourceUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));


        Account sourceAccount = accountRepository.findById(sourceUser.getUserAccount().getAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found!"));

        Account destinationAccount = accountRepository.findById(destinationAccountId)
                .orElseThrow(() -> new RuntimeException("Destination account not found!"));

        Transaction transaction = getTransaction(amount, sourceAccount, destinationAccount);
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));
        transactionRepository.save(transaction);

        return new CreationTransactionResponseDto(sourceAccount.getUserAccount().getUsername(), sourceAccount.getAccountNumber(),
                destinationAccount.getUserAccount().getUsername(), destinationAccount.getAccountNumber(),
                amount, transaction.getTransactionId());
    }

    private Transaction getTransaction(@NotNull BigDecimal amount, @NotNull Account sourceAccount, @NotNull Account destinationAccount) {
        Transaction transaction = new Transaction();


        if(sourceAccount.getBalance().compareTo(amount) < 0 || amount.signum() < 0){
            throw new RuntimeException("You don't have enough money");
        }
        if(sourceAccount == destinationAccount){
            throw new RuntimeException("You can't make a transaction to yourself");
        }
        transaction.setAmount(amount);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        return transaction;
    }
    
    

    @Transactional
    public Page<TransactionProjection> getTransactions(Pageable pageable){
        return transactionRepository.findAllBy(pageable);
    }
}
