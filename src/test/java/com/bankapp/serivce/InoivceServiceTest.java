package com.bankapp.serivce;

import com.bankapp.dto.Installments.PayInstallmentRequestDto;
import com.bankapp.entity.Invoice;
import com.bankapp.entity.User;
import com.bankapp.entity.Account;
import com.bankapp.entity.Installment;
import com.bankapp.entity.Card;
import com.bankapp.repository.InstallmentRepository;
import com.bankapp.repository.InvoiceRepository;
import com.bankapp.repository.UserRepository;
import com.bankapp.service.InstallmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InoivceServiceTest {

    @InjectMocks
    private InstallmentService installmentService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InstallmentRepository installmentRepository;


    @Test
    void shouldThrowExceptionWhenInstallmentIsAlreadyPaid() {
        //Arrange
        PayInstallmentRequestDto request = new PayInstallmentRequestDto(1L, 1L);
        User user = new User();
        user.setEmail("teste@gmail.com");
        Account userAccount = new Account();
        userAccount.setBalance(BigDecimal.valueOf(100));
        Card card = new Card();
        card.setCardId(10L);
        userAccount.setCardAccount(card);
        user.setUserAccount(userAccount);

        //Auth
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        Invoice invoice = new Invoice();
        invoice.setInvoiceId(1L);
        invoice.setTotalAmount(BigDecimal.valueOf(100));
        invoice.setInstallmentCount(1);
        invoice.setCreditCard(card);
        List<Installment> listInstallment = new ArrayList<>();

        Installment newInstallment = new Installment();
        newInstallment.setAmount(BigDecimal.valueOf(10));
        newInstallment.setPaid(true);
        newInstallment.setInvoice(invoice);
        newInstallment.setInstallmentNumber(1);
        listInstallment.add(newInstallment);
        invoice.setInstallments(listInstallment);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(invoiceRepository.findById(1L))
                .thenReturn(Optional.of(invoice));
        when(installmentRepository.findById(1L))
                .thenReturn(Optional.of(newInstallment));

        when(securityContext.getAuthentication()).
                thenReturn(authentication);
        when(authentication.getPrincipal()).
                thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        //Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> installmentService.payInstallment(request)
        );
        assertEquals("Installment is already paid!", exception.getMessage());
    }
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
