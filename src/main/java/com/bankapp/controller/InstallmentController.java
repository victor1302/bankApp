package com.bankapp.controller;

import com.bankapp.dto.Installments.PayInstallmentRequestDto;
import com.bankapp.dto.Installments.PayInstallmentResponseDto;
import com.bankapp.service.InstallmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class InstallmentController {

    private final InstallmentService installmentService;

    public InstallmentController(InstallmentService installmentService) {
        this.installmentService = installmentService;
    }

    @PostMapping("/installment")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_BASIC')")
    public ResponseEntity<PayInstallmentResponseDto> payInstallment(@RequestBody PayInstallmentRequestDto payInstallmentRequestDto){
        PayInstallmentResponseDto payInstallmentResponseDto = installmentService.payInstallment(payInstallmentRequestDto);
        return ResponseEntity.ok(payInstallmentResponseDto);
    }
}
