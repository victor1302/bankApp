package com.bankapp.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;

@Entity
@Table(name = "tb_invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "invoice_id")
    private Long invoiceId;
    private YearMonth referenceMonth;
    private Instant closingDate;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private InvoiceStatus status;
    @CreationTimestamp
    private Instant creationTimestamp;
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card creditCard;

    public enum InvoiceStatus{
        GENERATED,
        CLOSED,
        INSTALLMENT_PLAN,
        PAID,
        OVERDUE,
        CANCELED
    }
}


