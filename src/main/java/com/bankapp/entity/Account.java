package com.bankapp.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "tb_accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "account_id")
    private Long accountId;
    private int accountNumber;
    private BigDecimal balance;
    @CreationTimestamp
    private Instant creationTimestamp;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User userAccount;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", unique = true)
    private Card cardAccount;


    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public User getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(User userAccount) {
        this.userAccount = userAccount;
    }

    public Instant getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Instant creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Card getCardAccount() {
        return cardAccount;
    }

    public void setCardAccount(Card cardAccount) {
        this.cardAccount = cardAccount;
    }
}
