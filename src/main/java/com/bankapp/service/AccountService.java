package com.bankapp.service;

import com.bankapp.entity.Account;
import com.bankapp.entity.User;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.UserRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;


    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Account createAccount(JwtAuthenticationToken userToken){
        UUID userId = UUID.fromString(userToken.getName());

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("Usuário não encontrado"));

        Integer lastAccountNumber = accountRepository.findMaxAccountNumber();
        int newAccountNumber = (lastAccountNumber != null) ? lastAccountNumber + 1 : 1;

        Account newAccount = new Account();
        newAccount.setUserAccount(user);
        newAccount.setBalance(BigDecimal.valueOf(0));
        newAccount.setAccountNumber(newAccountNumber);

        user.setUserAccount(newAccount);
        userRepository.save(user);

        return userRepository.save(user).getUserAccount();
    }

}
