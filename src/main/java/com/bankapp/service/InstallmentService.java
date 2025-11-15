package com.bankapp.service;

import com.bankapp.dto.Installments.PayInstallmentRequestDto;
import com.bankapp.dto.Installments.PayInstallmentResponseDto;
import com.bankapp.entity.Card;
import com.bankapp.entity.Installment;
import com.bankapp.entity.Invoice;
import com.bankapp.entity.User;
import com.bankapp.repository.CardRepository;
import com.bankapp.repository.InstallmentRepository;
import com.bankapp.repository.InvoiceRepository;
import com.bankapp.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InstallmentService {

    private final InstallmentRepository installmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;


    public InstallmentService(InstallmentRepository installmentRepository, InvoiceRepository invoiceRepository, CardRepository cardRepository, UserRepository userRepository) {
        this.installmentRepository = installmentRepository;
        this.invoiceRepository = invoiceRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PayInstallmentResponseDto payInstallment(PayInstallmentRequestDto payInstallmentRequestDto){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User sourceUser = userRepository.findByEmail(user.getEmail()).
                orElseThrow();
        Invoice invoice = invoiceRepository.findById(payInstallmentRequestDto.invoiceId())
                .orElseThrow( () -> new RuntimeException("dont found invoice"));

        Installment installmentToPay = installmentRepository.findById(payInstallmentRequestDto.installmentId())
                .orElseThrow( () -> new RuntimeException("dont found installment"));

        Card cardUser = sourceUser.getUserAccount().getCardAccount();

        if(!(sourceUser.getUserAccount().getCardAccount().getCardId().equals(invoice.getCreditCard().getCardId()))){
            throw new RuntimeException("You cant pay others installments");
        }

        if(sourceUser.getUserAccount().getBalance().compareTo(installmentToPay.getAmount()) < 0){
            throw new RuntimeException("You don't have money to pay this installment");
        }
        if(installmentToPay.isPaid()){
            throw new RuntimeException("Installment is already paid!");
        }
        installmentToPay.setPaid(true);
        installmentToPay.setPaymentDate(LocalDateTime.now());
        installmentRepository.save(installmentToPay);

        invoice.setAmountPaid(invoice.getAmountPaid().add(installmentToPay.getAmount()));
        invoice.setUpdatedAt(Instant.now());
        invoiceRepository.save(invoice);

        cardUser.setAvailableLimit(cardUser.getAvailableLimit().add(installmentToPay.getAmount()));
        cardRepository.save(cardUser);

        return new PayInstallmentResponseDto(true);
    }
}
