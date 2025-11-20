package com.bankapp.interfaces;


import java.math.BigDecimal;
import java.time.Instant;

public interface TransactionProjection {

    Long getTransactionId();
    Instant getCreationTimestamp();
    AccountProjection getSourceAccount();
    AccountProjection getDestinationAccount();
    BigDecimal getAmount();

}
