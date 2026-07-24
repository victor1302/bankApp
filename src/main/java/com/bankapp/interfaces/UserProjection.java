package com.bankapp.interfaces;


import java.util.UUID;

public interface UserProjection {
    UUID getUserId();
    String getUsername();
    String getEmail();
    AccountProjection getUserAccount();
    String getAddress();
    int getAge();
    boolean getIsActive();
}
