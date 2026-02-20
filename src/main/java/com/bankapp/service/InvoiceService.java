package com.bankapp.service;

import com.bankapp.dto.Invoice.CreateInvoiceResponseDto;
import com.bankapp.dto.Invoice.CreateInvoiceRequestDto;
import com.bankapp.entity.*;
import com.bankapp.entity.enums.InvoiceStatus;
import com.bankapp.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;

@Service
public class InvoiceService {

    private final CardRepository cardRepository;
    private final InstallmentService installmentService;
    private final AccountRepository accountRepository;
    private final InvoiceRepository invoceRepository;

    public InvoiceService(CardRepository cardRepository, InstallmentService installmentService, AccountRepository accountRepository, InvoiceRepository invoceRepository) {
        this.cardRepository = cardRepository;
        this.installmentService = installmentService;
        this.accountRepository = accountRepository;
        this.invoceRepository = invoceRepository;
    }

    @Transactional
    public CreateInvoiceResponseDto createInvoice(CreateInvoiceRequestDto crateInvoiceRequestDto){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account sourceAccount = accountRepository.findById(user.getUserAccount().getAccountId()).
                orElseThrow( () -> new RuntimeException("Not found source account"));

        if(sourceAccount.getCardAccount() != null && sourceAccount.getCardAccount().isBlocked()){
            throw new RuntimeException("Account don't have a card or is blocked");
        }
        Card sourceAccountCard = sourceAccount.getCardAccount();

        if(sourceAccountCard.getAvailableLimit().compareTo(crateInvoiceRequestDto.totalAmount()) < 0
                || crateInvoiceRequestDto.totalAmount().signum() < 0){
            throw new RuntimeException("You don't have sufficient limit available");
        }

        YearMonth yearMonth = YearMonth.now();
        Invoice newInvoice = new Invoice();
        newInvoice.setReferenceMonth(yearMonth);

        newInvoice.setTotalAmount(crateInvoiceRequestDto.totalAmount());
        newInvoice.setAmountPaid(BigDecimal.ZERO);
        newInvoice.setInstallmentCount(crateInvoiceRequestDto.installmentCount());
        newInvoice.setStatus(InvoiceStatus.OPEN);
        newInvoice.setDescription(crateInvoiceRequestDto.description());
        newInvoice.setClosingDate(yearMonth.plusMonths(crateInvoiceRequestDto.installmentCount()));
        newInvoice.setCreditCard(sourceAccountCard);
        newInvoice.setInstallments(installmentService.createInstallments(newInvoice));

        if(sourceAccountCard.getCardInvoice() == null){
            sourceAccountCard.setCardInvoice(new ArrayList<>());
        }


        sourceAccountCard.getCardInvoice().add(newInvoice);
        sourceAccountCard.setAvailableLimit(sourceAccountCard.getAvailableLimit().subtract(crateInvoiceRequestDto.totalAmount()));
        cardRepository.save(sourceAccountCard);
        return new CreateInvoiceResponseDto(newInvoice.getTotalAmount(), newInvoice.getInstallmentCount(), newInvoice.getDescription());
    }

    @Transactional
    public void payInvoice(Long invoiceId, BigDecimal amount){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account sourceAccount = accountRepository.findById(user.getUserAccount().getAccountId()).
                orElseThrow( () -> new RuntimeException("Not found source account"));

        Invoice invoice = invoceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Not found the invoice"));
        invoice.setAmountPaid(invoice.getAmountPaid().add(amount));
        if(invoice.getAmountPaid().compareTo(invoice.getTotalAmount()) >= 0){
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setUpdatedAt(Instant.now());
        }
        Card card = invoice.getCreditCard();
        card.setAvailableLimit(card.getAvailableLimit().add(amount));
        cardRepository.save(card);

    }


}
