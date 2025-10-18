package com.bankapp.interfaces;

import com.bankapp.entity.Account;

import java.util.UUID;

public interface UserProjection {
    UUID getUserId();
    String getUsername();
    AccountProjection getUserAccount();
    String getAddress();
    int getAge();
    boolean getisActive();
}
