package com.bankapp.service;

import com.bankapp.dto.Invoice.CreateInvoiceResponseDto;
import com.bankapp.dto.Invoice.CrateInvoiceRequestDto;
import com.bankapp.entity.*;
import com.bankapp.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {

    private final CardRepository cardRepository;
    private final InvoiceRepository invoiceRepository;
    private final InstallmentRepository installmentRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public InvoiceService(CardRepository cardRepository, InvoiceRepository invoiceRepository, InstallmentRepository installmentRepository, AccountRepository accountRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.invoiceRepository = invoiceRepository;
        this.installmentRepository = installmentRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public CreateInvoiceResponseDto createInvoice(CrateInvoiceRequestDto crateInvoiceRequestDto){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User sourceUser = userRepository.findByEmail(user.getEmail()).
                orElseThrow();
        Account userAccount = sourceUser.getUserAccount();
        Card userCard = sourceUser.getUserAccount().getCardAccount();

        YearMonth yearMonth = YearMonth.now();
        Invoice newInvoice = new Invoice();
        newInvoice.setReferenceMonth(yearMonth);

        if(userCard.getAvailableLimit().compareTo(crateInvoiceRequestDto.totalAmount()) < 0
                || crateInvoiceRequestDto.totalAmount().signum() < 0){
            throw new RuntimeException("You don't have sufficient limit available");
        }

        newInvoice.setTotalAmount(crateInvoiceRequestDto.totalAmount());
        newInvoice.setAmountPaid(BigDecimal.ZERO);
        newInvoice.setInstallmentCount(crateInvoiceRequestDto.installmentCount());
        newInvoice.setStatus(Invoice.InvoiceStatus.OPEN);
        newInvoice.setDescription(crateInvoiceRequestDto.description());
        newInvoice.setClosingDate(yearMonth.plus(Period.ofYears(crateInvoiceRequestDto.installmentCount())));
        newInvoice.setCreditCard(userCard);
        newInvoice.setInstallments(createInstallment(crateInvoiceRequestDto.totalAmount(),LocalDateTime.now(),crateInvoiceRequestDto.installmentCount(),
                newInvoice));

        invoiceRepository.save(newInvoice);
        return new CreateInvoiceResponseDto(newInvoice.getTotalAmount(), newInvoice.getInstallmentCount(), newInvoice.getDescription());
    }

    public List<Installment> createInstallment(BigDecimal amount, LocalDateTime firstDueDate, int installmentNumber, Invoice invoice){
        List<Installment> listInstallment = new ArrayList<>();
        for(int i = 0; i < installmentNumber; i++){
            Installment newInstallment = new Installment();
            newInstallment.setAmount(amount.divide(BigDecimal.valueOf(installmentNumber),2, RoundingMode.HALF_UP));
            newInstallment.setPaid(false);
            newInstallment.setInvoice(invoice);
            newInstallment.setInstallmentNumber(i + 1);

            LocalDateTime dueDate = firstDueDate.plusMonths(i);
            newInstallment.setDueDate(dueDate);
            listInstallment.add(newInstallment);
        }
        return listInstallment;
    }






}
