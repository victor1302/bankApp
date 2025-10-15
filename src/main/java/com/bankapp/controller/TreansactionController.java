package com.bankapp.controller;

import com.bankapp.dto.Transaction.CreateTransactionDto;
import com.bankapp.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TreansactionController {

    private final TransactionService transactionService;

    public TreansactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    @PreAuthorize("hasAnyAuthority('SCOPE_BASIC', 'SCOPE_ADMIN')")
    public ResponseEntity<Void> createTransaction(JwtAuthenticationToken jwtAuthenticationToken, @RequestBody CreateTransactionDto createTransactionDto){
        transactionService.createTransaction(jwtAuthenticationToken, createTransactionDto.destinationAccountId(), createTransactionDto.amount());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
