package com.bankapp.interfaces;

import java.math.BigDecimal;

public interface AccountProjection {
    Long getAccountId();
    int getAccountNumber();
    BigDecimal getBalance();
}
