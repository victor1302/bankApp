package com.bankapp.interfaces;


import com.bankapp.entity.enums.TransactionStatus;
import com.bankapp.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public interface TransactionProjection {

    Long getTransactionId();
    Instant getCreationTimestamp();
    AccountProjection getSourceAccount();
    AccountProjection getDestinationAccount();
    BigDecimal getAmount();
    TransactionType getTransactionType();

}
