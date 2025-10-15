package com.bankapp.controller;

import com.bankapp.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private final AccountService accountService;


    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/account")
    @PreAuthorize("hasAuthority('SCOPE_BASIC')")
    public ResponseEntity<Void> createAccount(JwtAuthenticationToken token){
        accountService.createAccount(token);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
