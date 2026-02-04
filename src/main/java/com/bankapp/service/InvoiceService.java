package com.bankapp.service;

import com.bankapp.dto.Invoice.CreateInvoiceResponseDto;
import com.bankapp.dto.Invoice.CrateInvoiceRequestDto;
import com.bankapp.entity.*;
import com.bankapp.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final InstallmentService installmentService;

    public InvoiceService(InvoiceRepository invoiceRepository, UserRepository userRepository, CardRepository cardRepository, InstallmentService installmentService) {
        this.invoiceRepository = invoiceRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.installmentService = installmentService;
    }

    @Transactional
    public CreateInvoiceResponseDto createInvoice(CrateInvoiceRequestDto crateInvoiceRequestDto){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User sourceUser = userRepository.findByEmail(user.getEmail()).
                orElseThrow();
        Card userCard = sourceUser.getUserAccount().getCardAccount();

        if(userCard.getAvailableLimit().compareTo(crateInvoiceRequestDto.totalAmount()) < 0
                || crateInvoiceRequestDto.totalAmount().signum() < 0){
            throw new RuntimeException("You don't have sufficient limit available");
        }

        YearMonth yearMonth = YearMonth.now();
        Invoice newInvoice = new Invoice();
        newInvoice.setReferenceMonth(yearMonth);

        newInvoice.setTotalAmount(crateInvoiceRequestDto.totalAmount());
        newInvoice.setAmountPaid(BigDecimal.ZERO);
        newInvoice.setInstallmentCount(crateInvoiceRequestDto.installmentCount());
        newInvoice.setStatus(Invoice.InvoiceStatus.OPEN);
        newInvoice.setDescription(crateInvoiceRequestDto.description());
        newInvoice.setClosingDate(yearMonth.plusMonths(crateInvoiceRequestDto.installmentCount()));
        newInvoice.setCreditCard(userCard);
        newInvoice.setInstallments(installmentService.createInstallments(newInvoice));
        if(userCard.getCardInvoice() == null){
            userCard.setCardInvoice(new ArrayList<>());
        }
        userCard.getCardInvoice().add(newInvoice);
        userCard.setAvailableLimit(userCard.getAvailableLimit().subtract(crateInvoiceRequestDto.totalAmount()));
        cardRepository.save(userCard);
        return new CreateInvoiceResponseDto(newInvoice.getTotalAmount(), newInvoice.getInstallmentCount(), newInvoice.getDescription());
    }



}
