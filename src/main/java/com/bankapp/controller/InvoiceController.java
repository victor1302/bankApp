package com.bankapp.controller;

import com.bankapp.dto.Invoice.CreateInvoiceResponseDto;
import com.bankapp.dto.Invoice.CrateInvoiceRequestDto;
import com.bankapp.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/invoice")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_BASIC')")
    public ResponseEntity<CreateInvoiceResponseDto> createInvoice(@RequestBody CrateInvoiceRequestDto crateInvoiceRequestDto){
        CreateInvoiceResponseDto createInvoiceResponseDto = invoiceService.createInvoice(crateInvoiceRequestDto);
        return ResponseEntity.ok(createInvoiceResponseDto);
    }

}
