package com.bankapp.entity.enums;

import java.math.BigDecimal;

public enum EntryType {
    DEBIT{
        @Override
        public BigDecimal apply(BigDecimal value){
            return value.negate();
        }
    },
    CREDIT{
        @Override
        public BigDecimal apply(BigDecimal value){
            return value;
        }
    };
    public abstract BigDecimal apply(BigDecimal value);
}
