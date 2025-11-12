package com.bankapp.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "tb_installment")
public class Installment{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "installment_id")
    private Long installmentId;
    private Integer installmentNumber;
    private BigDecimal amount;
    private Instant dueDate;
    private Instant paymentDate;
    private boolean paid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

}
