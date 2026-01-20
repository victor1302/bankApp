package com.bankapp.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_ledger")
public class LedgerEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ledger_id")
    private UUID ledgerId;
    private Long accountId;
    private BigDecimal amount;
    private Long referenceId;
    private String description;
    @CreationTimestamp
    private Instant createdAt;
    @Enumerated(EnumType.STRING)
    private ReferenceType referenceType;
    @Enumerated(EnumType.STRING)
    private EntryStatus entryStatus;
    @Enumerated(EnumType.STRING)
    private EntryType entryType;

    enum ReferenceType{
        INVOICE,
        INSTALLMENT,
        TRANSFER,
        REFUND,
        FEE
    }
    enum EntryStatus{
        PENDING,
        POSTED,
        CANCELED
    }
    enum EntryType{
        DEBIT,
        CREDIT
    }


}
