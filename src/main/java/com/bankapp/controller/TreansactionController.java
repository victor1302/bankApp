package com.bankapp.controller;

import com.bankapp.dto.Transaction.CreateTransactionDto;
import com.bankapp.dto.Transaction.CreationTransactionResponseDto;
import com.bankapp.interfaces.TransactionProjection;
import com.bankapp.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TreansactionController {

    private final TransactionService transactionService;

    public TreansactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_BASIC')")
    public ResponseEntity<CreationTransactionResponseDto> createTransaction(@RequestBody CreateTransactionDto createTransactionDto){
        CreationTransactionResponseDto creationTransactionResponseDto = transactionService.createPixTransaction(createTransactionDto);
        return ResponseEntity.ok(creationTransactionResponseDto);
    }

    @GetMapping("/transaction")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<TransactionProjection>> getTransactions(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<TransactionProjection> transactions = transactionService.getTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }
}
