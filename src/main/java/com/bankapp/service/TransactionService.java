package com.bankapp.service;

import com.bankapp.entity.Account;
import com.bankapp.entity.Transaction;
import com.bankapp.entity.User;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.TransactionRepository;
import com.bankapp.repository.UserRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    public Transaction createTransaction(JwtAuthenticationToken jwtAuthenticationToken, Long destinationAccountId, BigDecimal amount){
        UUID userId = UUID.fromString(jwtAuthenticationToken.getName());
        User sourceUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Account sourceAccount = accountRepository.findById(sourceUser.getUserAccount().getAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found!"));

        Account destinationAccount = accountRepository.findById(destinationAccountId)
                .orElseThrow(() -> new RuntimeException("Destination account not found!"));

        Transaction transaction = new Transaction();
        if(sourceAccount.getBalance().compareTo(amount) < 0){
            new RuntimeException("You dont have money enought");
        }
        transaction.setAmount(amount);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        return transactionRepository.save(transaction);
    }
}
