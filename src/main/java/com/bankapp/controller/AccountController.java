package com.bankapp.controller;

import com.bankapp.dto.Account.CreateAccountResponseDto;
import com.bankapp.entity.Account;
import com.bankapp.interfaces.AccountProjection;
import com.bankapp.repository.AccountRepository;
import com.bankapp.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1")
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
    @PutMapping("/account/disable/{accountId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> disableAccount(@PathVariable Long accountId){
        accountService.disableAccount(accountId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/account")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<AccountProjection>> getAccounts(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("accountNumber").ascending());
        Page<AccountProjection> accounts = accountService.getAccounts(pageable);
        return ResponseEntity.ok(accounts);
    }

}
