package com.bankapp.controller;

import com.bankapp.dto.Account.CreateAccountResponseDto;
import com.bankapp.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private final AccountService accountService;


    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/account")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_BASIC')")
    public ResponseEntity<CreateAccountResponseDto> createAccount(){
        CreateAccountResponseDto createAccountResponseDto = accountService.createAccount();
        return ResponseEntity.ok(createAccountResponseDto);
    }
}
