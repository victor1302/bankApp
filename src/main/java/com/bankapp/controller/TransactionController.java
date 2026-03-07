package com.bankapp.controller;

import com.bankapp.dto.Transaction.*;
import com.bankapp.interfaces.TransactionProjection;
import com.bankapp.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_BASIC')")
    public ResponseEntity<CreateTransactionResponseDto> createTransaction(@RequestBody CreateTransactionDto createTransactionDto){
        CreateTransactionResponseDto creationTransactionResponseDto = transactionService.createPixTransaction(createTransactionDto);
        return ResponseEntity.ok(creationTransactionResponseDto);
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<TransactionProjection>> getTransactions(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<TransactionProjection> transactions = transactionService.getTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }
    @PostMapping("/transactions/credit")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_BASIC')")
    public ResponseEntity<CreateCreditResponse> createInvoiceTransaction(@RequestBody CreateCreditRequest createCreditRequest){
        CreateCreditResponse creationTransactionResponseDto = transactionService.createCreditTransaction(createCreditRequest);
        return ResponseEntity.ok(creationTransactionResponseDto);
    }
    @PutMapping("/transactions/pay/invoice/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_BASIC')")
    public ResponseEntity<PayInvoiceResponse> payFullInvoice(@PathVariable Long id){
        PayInvoiceResponse response = transactionService.payFullInvoice(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/transactions/{accountId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_BASIC')")
    public ResponseEntity<Map<String, Page<TransactionProjection>>> getTransactionByUser(@PathVariable Long accountId,
                                                                                         @PageableDefault(size = 5, sort = "transactionId",
    direction = Sort.Direction.DESC) Pageable pageable){
        Map<String, Page<TransactionProjection>> transactions = transactionService.getTransactionByUser(accountId,pageable);
        return ResponseEntity.ok(transactions);
    }
}
