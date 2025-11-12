package com.bankapp.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

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
    @CreationTimestamp
    private Instant creationTimestamp;

    @OneToOne(mappedBy = "cardAccount", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
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
}
