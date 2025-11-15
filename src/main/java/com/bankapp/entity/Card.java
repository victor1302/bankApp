package com.bankapp.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "card_id")
    private Long cardId;
    private String pan;
    private String cvv;
    private String expiry;
    private BigDecimal creditLimit;
    private BigDecimal availableLimit;
    private boolean isBlocked;

    @OneToMany(mappedBy = "invoiceId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> cardInvoice = new ArrayList<>();

    @CreationTimestamp
    private Instant creationTimestamp;

    @OneToOne(mappedBy = "cardAccount", cascade = CascadeType.PERSIST)
    private Account cardAccount;

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }


    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public Instant getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Instant creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Account getCardAccount() {
        return cardAccount;
    }

    public void setCardAccount(Account cardAccount) {
        this.cardAccount = cardAccount;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getAvailableLimit() {
        return availableLimit;
    }

    public void setAvailableLimit(BigDecimal availableLimit) {
        this.availableLimit = availableLimit;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public List<Invoice> getCardInvoice() {
        return cardInvoice;
    }

    public void setCardInvoice(List<Invoice> cardInvoice) {
        this.cardInvoice = cardInvoice;
    }
}
