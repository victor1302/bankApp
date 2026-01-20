package com.bankapp.service;

import com.bankapp.dto.Account.CreateAccountResponseDto;
import com.bankapp.entity.Account;
import com.bankapp.entity.User;
import com.bankapp.exception.AlreadyDisabledOrNotPresent;
import com.bankapp.exception.AlreadyExistsException;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;


    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CreateAccountResponseDto createAccount(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userRepository.findByEmail(user.getEmail()).orElseThrow();
        Integer lastAccountNumber = accountRepository.findMaxAccountNumber();
        int newAccountNumber = (lastAccountNumber != null) ? lastAccountNumber + 1 : 1;
        Account newAccount = new Account(user, newAccountNumber);
        return new CreateAccountResponseDto(newAccount.getAccountNumber(), newAccount.getCachedBalance());
    }
    @Transactional
    public Account disableAccount(Long accountId){
        return accountRepository.findById(accountId)
                .map(account ->{
                    if(!account.isActive()){
                        throw new AlreadyDisabledOrNotPresent("Account already disabled or not present");
                    }
                    account.setActive(false);
                    return accountRepository.save(account);
                }).orElseThrow(()-> new AlreadyExistsException("Account not present or already exists"));
    }

}
