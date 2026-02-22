package com.bankapp.interfaces;


import com.bankapp.entity.Card;
import com.bankapp.entity.enums.AccountType;

import java.math.BigDecimal;

public interface AccountProjection {
    int getAccountNumber();
    AccountType getAccountType();
    BigDecimal getCachedBalance();
}
